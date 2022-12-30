package tk.zulfengaming.zulfbungee.universal.socket;


import tk.zulfengaming.zulfbungee.universal.interfaces.StorageImpl;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.storage.db.H2Impl;
import tk.zulfengaming.zulfbungee.universal.storage.db.MySQLImpl;
import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.command.ChatColour;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ScriptAction;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ServerInfo;
import tk.zulfengaming.zulfbungee.universal.task.ProxyTaskManager;

import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainServer implements Runnable {
    // plugin instance !!!

    private final ZulfBungeeProxy pluginInstance;

    // setting up the server
    private final int port;
    private final InetAddress hostAddress;

    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicBoolean serverSocketAvailable = new AtomicBoolean(false);

    // hey, keep that to yourself!
    private ServerSocket serverSocket;
    private Socket socket;

    private final ArrayList<BaseServerConnection> socketConnections = new ArrayList<>();

    private final HashMap<String, BaseServerConnection> activeConnections = new HashMap<>();

    // quite neat
    private final PacketHandlerManager packetManager;
    private final ProxyTaskManager taskManager;

    // storage
    private StorageImpl storage;

    public MainServer(int port, InetAddress address, ZulfBungeeProxy instanceIn) {
        this.hostAddress = address;
        this.port = port;
        this.pluginInstance = instanceIn;

        this.packetManager = new PacketHandlerManager(this);
        this.taskManager = instanceIn.getTaskManager();

        Optional<StorageImpl> newStorage = setupStorage();

        if (newStorage.isPresent()) {

            storage = newStorage.get();

            pluginInstance.getTaskManager().newTask(() -> storage.setupDatabase());

            pluginInstance.logDebug(ChatColour.GREEN + "Currently using StorageImpl: " + storage.getClass().toString());

        }

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

    private void acceptConnection(Socket socketIn) throws IOException {

        BaseServerConnection connection = new BaseServerConnection(this, socketIn);

        taskManager.newTask(connection);
        socketConnections.add(connection);

        pluginInstance.logInfo(ChatColour.GREEN + "Connection established with address: " + connection.getAddress());

    }

    // TODO: Add address whitelist
    private boolean isValidClient(SocketAddress addressIn) {

        Map<String, ServerInfo> servers = pluginInstance.getServersCopy();

        boolean portWhitelistEnabled = pluginInstance.getConfig().getBoolean("port-whitelist");
        List<Integer> ports = pluginInstance.getConfig().getIntList("ports");

        for (ServerInfo server : servers.values()) {

            InetSocketAddress inetServerAddr = (InetSocketAddress) server.getSocketAddress();
            InetSocketAddress inetAddrIn = (InetSocketAddress) addressIn;

            if (inetServerAddr.getAddress().equals(inetAddrIn.getAddress())) {

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
        for (BaseServerConnection connection : socketConnections) {
            connection.sendDirect(packetIn);
        }
    }

    public void syncScriptsFolder(Map<String, ScriptAction> scriptNamesIn, ProxyCommandSender senderIn) {

        for (BaseServerConnection connection : socketConnections) {

            for (Map.Entry<String, ScriptAction> script : scriptNamesIn.entrySet()) {

                Path scriptPath = pluginInstance.getConfig().getScriptPath(script.getKey());

                connection.sendScript(scriptPath, script.getValue(), senderIn);

            }

        }

    }

    public void addActiveConnection(BaseServerConnection connection, String name) {

        activeConnections.put(name, connection);

        pluginInstance.logDebug("Server '" + name + "' added to the list of active connections!");

        sendDirectToAll(new Packet(PacketTypes.PROXY_CLIENT_INFO, false, true, getProxyServerArray()));

    }

    public void removeServerConnection(BaseServerConnection connectionIn) {

        socketConnections.remove(connectionIn);
        String connectionName = connectionIn.getName();

        if (!connectionName.isEmpty()) {
            activeConnections.remove(connectionName);
            pluginInstance.logInfo(String.format(ChatColour.YELLOW + "Disconnecting client %s (%s)", connectionIn.getAddress(), connectionName));
            sendDirectToAll(new Packet(PacketTypes.PROXY_CLIENT_INFO, false, true, connectionName));
        }



    }

    public void end() throws IOException {

        if (running.compareAndSet(true, false)) {

            for (BaseServerConnection connection : socketConnections) {
                connection.shutdown();
            }

            activeConnections.clear();
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

    private Optional<StorageImpl> setupStorage() {

        StorageImpl newStorage = null;

        String storageChoice = pluginInstance.getConfig().getString("storage-type");

        if (storageChoice.matches("(?i)mysql")) {
            newStorage = new MySQLImpl(this);
        } else if (storageChoice.matches("(?i)h2")) {
            newStorage = new H2Impl(this);
        }

        return Optional.ofNullable(newStorage);


    }

    public Set<String> getServerNames() {
        return activeConnections.keySet();
    }

    public BaseServerConnection getConnectionFromName(String name) {
        return activeConnections.get(name);
    }

    public ProxyServer[] getProxyServerArray() {
        return activeConnections.entrySet().stream()
                .map(proxyServerList -> new ProxyServer(proxyServerList.getKey(), proxyServerList.getValue().getServerInfo()))
                .toArray(ProxyServer[]::new);
    }


    public PacketHandlerManager getPacketManager() {
        return packetManager;
    }

    public Optional<StorageImpl> getStorage() {
        return Optional.ofNullable(storage);
    }

    public ZulfBungeeProxy getPluginInstance() {
        return pluginInstance;
    }

}

