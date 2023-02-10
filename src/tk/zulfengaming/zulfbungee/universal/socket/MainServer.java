package tk.zulfengaming.zulfbungee.universal.socket;


import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.command.util.ChatColour;
import tk.zulfengaming.zulfbungee.universal.interfaces.StorageImpl;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.managers.ProxyTaskManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptAction;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfServerInfo;
import tk.zulfengaming.zulfbungee.universal.storage.db.H2Impl;
import tk.zulfengaming.zulfbungee.universal.storage.db.MySQLImpl;

import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public abstract class MainServer<P> implements Runnable {
    // plugin instance !!!

    private final ZulfBungeeProxy<P> pluginInstance;

    // setting up the server
    private final int port;
    private final InetAddress hostAddress;

    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicBoolean serverSocketAvailable = new AtomicBoolean(false);

    // hey, keep that to yourself!
    private ServerSocket serverSocket;
    private Socket socket;

    private final ArrayList<BaseServerConnection<P>> socketConnections = new ArrayList<>();

    private final HashMap<SocketAddress, String> addressNames = new HashMap<>();
    private final HashMap<String, BaseServerConnection<P>> activeConnections = new HashMap<>();

    // quite neat
    private final PacketHandlerManager<P> packetManager;
    private final ProxyTaskManager taskManager;

    // storage
    private StorageImpl<P> storage;

    public MainServer(int port, InetAddress address, ZulfBungeeProxy<P> instanceIn) {

        this.hostAddress = address;
        this.port = port;
        this.pluginInstance = instanceIn;

        this.packetManager = new PacketHandlerManager<>(this);
        this.taskManager = instanceIn.getTaskManager();

        pluginInstance.getTaskManager().newTask(() -> {

            Optional<StorageImpl<P>> newStorage = setupStorage();

            if (newStorage.isPresent()) {

                storage = newStorage.get();
                storage.setupDatabase();

                pluginInstance.logDebug(ChatColour.GREEN + "Currently using StorageImpl: " + storage.getClass().toString());

            }

        });

        pluginInstance.getUpdater().checkUpdate(pluginInstance.getConsole(), true);

    }


    public void run() {


        do {
            try {

                if (serverSocketAvailable.get()) {

                    socket = serverSocket.accept();

                    SocketAddress remoteAddress = socket.getRemoteSocketAddress();

                    if (isValidClient(remoteAddress)) {

                        acceptConnection(socket);

                    } else {

                        pluginInstance.warning("A connection to the proxy was established, but the security check failed!");
                        pluginInstance.warning("Please check your Bungeecord config.yml to see if the Spigot server is defined, or make sure the client" +
                                "'s port is defined in the port whitelist section of the ZulfBungee config, if it's enabled!");

                        pluginInstance.warning("");
                        pluginInstance.warning("Address that tried to connect: " + remoteAddress.toString());

                        socket.close();
                    }


                } else {

                    try {

                        serverSocket = new ServerSocket(port, 50, hostAddress);

                    } catch (IOException e) {

                        pluginInstance.error("There was an error trying to start the server!");
                        pluginInstance.error("Please check your config to see if the port and host you specified is valid / not being used by another process.");
                        pluginInstance.error("Once you have done this, please restart this proxy server!");
                        pluginInstance.error("");
                        pluginInstance.error(e.toString());

                        break;
                    }

                    serverSocketAvailable.compareAndSet(false, true);

                    pluginInstance.logInfo(ChatColour.GREEN + "Waiting for connections on " + hostAddress + ":" + port);

                }

            } catch (SocketException | EOFException ignored) {

            } catch (IOException e) {
                pluginInstance.error("An error occurred while running the server!");
                pluginInstance.error("Please report this error on GitHub or directly to the devs:");
                pluginInstance.error("https://github.com/Zulfen/ZulfBungee/issues");
                pluginInstance.error("");

                e.printStackTrace();

                break;

            }

        } while (running.get());
    }

    protected abstract BaseServerConnection<P> newConnection(Socket socketIn) throws IOException;

    private void acceptConnection(Socket socketIn) throws IOException {

        BaseServerConnection<P> connection = newConnection(socketIn);

        taskManager.newTask(connection);
        socketConnections.add(connection);

        pluginInstance.logInfo(ChatColour.GREEN + "Connection established with address: " + connection.getAddress());

    }

    // TODO: Add address whitelist
    private boolean isValidClient(SocketAddress addressIn) {

        Map<String, ZulfServerInfo<P>> servers = pluginInstance.getServersCopy();

        boolean portWhitelistEnabled = pluginInstance.getConfig().getBoolean("port-whitelist");
        List<Integer> ports = pluginInstance.getConfig().getIntList("ports");

        InetSocketAddress inetAddrIn = ((InetSocketAddress) addressIn);

        boolean isLocalHost = false;
        boolean isPterodactyl = false;

        try {
            isLocalHost = inetAddrIn.getAddress().equals(InetAddress.getLocalHost());
            isPterodactyl = inetAddrIn.getAddress().equals(InetAddress.getByName("172.18.0.1"));
        } catch (UnknownHostException e) {
            pluginInstance.warning("Could not resolve localhost on this machine. Security checks may fail!");
        }

        for (ZulfServerInfo<P> server : servers.values()) {

            SocketAddress inetServerAddr = server.getSocketAddress();

            if (inetServerAddr.equals(inetAddrIn) || isLocalHost || isPterodactyl) {

                if (portWhitelistEnabled) {

                    return ports.contains(inetAddrIn.getPort());

                }

                return true;

            }

        }

        return false;
    }

    public void sendDirectToAllAsync(Packet packetIn) {
        taskManager.newTask(() -> sendDirectToAll(packetIn));
    }

    public void sendDirectToAll(Packet packetIn) {
        pluginInstance.logDebug("Sending packet " + packetIn.getType().toString() + " to all clients...");
        for (BaseServerConnection<P> connection : socketConnections) {
            connection.sendDirect(packetIn);
        }
    }

    public void syncScripts(Map<String, ScriptAction> scriptNamesIn, ProxyCommandSender<P> senderIn) {

        for (BaseServerConnection<P> connection : socketConnections) {

            for (Map.Entry<String, ScriptAction> script : scriptNamesIn.entrySet()) {

                Path scriptPath = pluginInstance.getConfig().getScriptPath(script.getKey());
                connection.sendScript(scriptPath, script.getValue(), senderIn);

            }

        }

    }

    public void addActiveConnection(BaseServerConnection<P> connection, String name) {

        addressNames.put(connection.getAddress(), name);
        activeConnections.put(name, connection);

        pluginInstance.logDebug("Server '" + name + "' added to the list of active connections!");
        sendDirectToAll(new Packet(PacketTypes.PROXY_CLIENT_INFO, false, true, getClientServerArray()));

    }

    public void removeServerConnection(BaseServerConnection<P> connectionIn) {

        socketConnections.remove(connectionIn);
        String name = addressNames.get(connectionIn.getAddress());

        if (name != null) {
            activeConnections.remove(name);
            pluginInstance.logInfo(String.format(ChatColour.YELLOW + "Disconnecting client %s (%s)", connectionIn.getAddress(), name));
            sendDirectToAll(new Packet(PacketTypes.PROXY_CLIENT_INFO, false, true, getClientServerArray()));
        }


    }

    public void end() throws IOException {

        if (running.compareAndSet(true, false)) {

            for (BaseServerConnection<P> connection : socketConnections) {
                connection.shutdown();
            }

            activeConnections.clear();
            addressNames.clear();
            socketConnections.clear();

            if (socket != null) {
                socket.close();
            }

            serverSocket.close();

            if (storage != null) {
                storage.shutdown();
            }

        }

    }

    private Optional<StorageImpl<P>> setupStorage() {

        StorageImpl<P> newStorage = null;

        String storageChoice = pluginInstance.getConfig().getString("storage-type");

        if (storageChoice.matches("(?i)mysql")) {
            newStorage = new MySQLImpl<>(this);
        } else if (storageChoice.matches("(?i)h2")) {
            newStorage = new H2Impl<>(this);
        }

        return Optional.ofNullable(newStorage);


    }

    public Set<String> getServerNames() {
        return activeConnections.keySet();
    }

    public Optional<String> getNameFromAddress(SocketAddress addressIn) {
        return Optional.ofNullable(addressNames.get(addressIn));
    }

    public BaseServerConnection<P> getConnectionFromName(String name) {
        return activeConnections.get(name);
    }

    public ClientServer[] getClientServerArray() {
        return activeConnections.entrySet().stream()
                .map(proxyServerList -> new ClientServer(proxyServerList.getKey(), proxyServerList.getValue().getClientInfo()))
                .toArray(ClientServer[]::new);
    }

    public List<ZulfProxyPlayer<P>> getProxyPlayersFrom(String nameIn) {

        BaseServerConnection<P> serverConnection = getConnectionFromName(nameIn);

        if (serverConnection != null) {
            return serverConnection.getPlayers();
        }

        return Collections.emptyList();

    }

    public List<ZulfProxyPlayer<P>> getAllPlayers() {
        return socketConnections.stream()
                .flatMap(connection -> connection.getPlayers().stream()).collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean areClientsConnected() {
        return socketConnections.size() > 0;
    }

    public PacketHandlerManager<P> getPacketManager() {
        return packetManager;
    }

    public Optional<StorageImpl<P>> getStorage() {
        return Optional.ofNullable(storage);
    }

    public ZulfBungeeProxy<P> getPluginInstance() {
        return pluginInstance;
    }

}

