package tk.zulfengaming.zulfbungee.universal.socket;


import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.command.util.ChatColour;
import tk.zulfengaming.zulfbungee.universal.interfaces.StorageImpl;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.managers.ProxyTaskManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientInfo;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptAction;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.storage.db.H2Impl;
import tk.zulfengaming.zulfbungee.universal.storage.db.MySQLImpl;

import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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

    private ServerSocket serverSocket;
    private Socket socket;

    private final CopyOnWriteArrayList<BaseServerConnection<P>> socketConnections = new CopyOnWriteArrayList<>();

    private final ConcurrentHashMap<SocketAddress, String> addressNames = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, BaseServerConnection<P>> activeConnections = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, ClientInfo> clientInfos = new ConcurrentHashMap<>();

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
                    acceptConnection(socket);


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

            } catch (SocketException | EOFException e) {

                if (pluginInstance.isDebug() && e instanceof EOFException) {
                    pluginInstance.warning("An uncommon error just occurred! This can be normal, but please report this to the developers!");
                    e.printStackTrace();
                }

                try {

                    if (socket != null) {
                        socket.close();
                    }

                } catch (IOException ioException) {
                    throw new RuntimeException(ioException);
                }


            } catch (IOException e) {

                if (pluginInstance.isDebug()) {
                    pluginInstance.warning("There was an error trying to establish a connection! Please consider restarting this proxy.");
                    e.printStackTrace();
                }

            }

        } while (running.get());
    }

    protected abstract BaseServerConnection<P> newConnection(Socket socketIn) throws IOException;

    private void acceptConnection(Socket socketIn) throws IOException {
        BaseServerConnection<P> connection = newConnection(socketIn);
        taskManager.newTask(connection);
        socketConnections.add(connection);
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

    public void syncScripts(Map<Path, ScriptAction> scriptNamesIn, ProxyCommandSender<P> senderIn) {

        for (BaseServerConnection<P> connection : socketConnections) {

            for (Map.Entry<Path, ScriptAction> script : scriptNamesIn.entrySet()) {
                String name = script.getKey().getFileName().toString();
                connection.sendScript(name, script.getKey(), script.getValue(), senderIn);
            }

        }

    }

    public void syncScripts(Map<String, Path> scriptNamesIn, ScriptAction scriptActionIn, ProxyCommandSender<P> senderIn) {

        for (BaseServerConnection<P> connection : socketConnections) {

            for (Map.Entry<String, Path> script : scriptNamesIn.entrySet()) {
                connection.sendScript(script.getKey(), script.getValue(), scriptActionIn, senderIn);
            }

        }

    }

    public void addActiveConnection(BaseServerConnection<P> connectionIn, String name, ClientInfo infoIn) {

        SocketAddress address = connectionIn.getAddress();

        if (!activeConnections.containsKey(name)) {

            addressNames.put(address, name);
            activeConnections.put(name, connectionIn);
            clientInfos.put(name, infoIn);

            pluginInstance.logInfo(String.format("%sConnection established with %s (%s)", ChatColour.GREEN, address, name));

            sendDirectToAll(new Packet(PacketTypes.PROXY_CLIENT_INFO, false, true, getClientServerArray()));

        } else {
            pluginInstance.warning(String.format("Server %s is already registered. Please change the forced-connection-name in the client's config to something different!", name));
            connectionIn.sendDirect(new Packet(PacketTypes.INVALID_CONFIGURATION, false, true, new Object[0]));
        }

    }

    public void removeServerConnection(BaseServerConnection<P> connectionIn) {

        socketConnections.remove(connectionIn);
        String name = addressNames.remove(connectionIn.getAddress());

        if (name != null) {

            activeConnections.remove(name);
            clientInfos.remove(name);

            pluginInstance.logInfo(String.format(ChatColour.YELLOW + "Disconnecting client %s (%s)", connectionIn.getAddress(), name));

            sendDirectToAll(new Packet(PacketTypes.PROXY_CLIENT_INFO, false, true, getClientServerArray()));

        } else {
            pluginInstance.logInfo(String.format(ChatColour.YELLOW + "Disconnecting client %s", connectionIn.getAddress()));
        }


    }

    public void end() throws IOException {

        if (running.compareAndSet(true, false)) {

            if (serverSocket != null) {
                serverSocket.close();
            }

            if (socket != null) {
                socket.close();
            }

            for (BaseServerConnection<P> connection : socketConnections) {
                connection.end();
            }

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


    public Optional<BaseServerConnection<P>> getConnection(String name) {
        return Optional.ofNullable(activeConnections.get(name));
    }

    public Optional<BaseServerConnection<P>> getConnection(ClientServer serverIn) {
        return Optional.ofNullable(activeConnections.get(serverIn.getName()));
    }

    public Optional<BaseServerConnection<P>> getConnection(ZulfProxyPlayer<P> playerIn) {
        return Optional.ofNullable(activeConnections.get(playerIn.getServer().getName()));
    }

    public List<ZulfProxyPlayer<P>> getProxyPlayersFrom(ClientServer clientServerIn) {

        Optional<BaseServerConnection<P>> serverConnection = getConnection(clientServerIn);

        if (serverConnection.isPresent()) {
            return serverConnection.get().getPlayers();
        }

        return Collections.emptyList();

    }

    public List<ZulfProxyPlayer<P>> getAllPlayers() {
        return socketConnections.stream()
                .flatMap(connection -> connection.getPlayers().stream()).collect(Collectors.toCollection(ArrayList::new));
    }

    private ClientServer[] getClientServerArray() {
        return clientInfos.entrySet().stream()
                .map(entry -> new ClientServer(entry.getKey(), entry.getValue()))
                .toArray(ClientServer[]::new);
    }

    public Optional<ClientInfo> getClientInfo(String nameIn) {
        return Optional.ofNullable(clientInfos.get(nameIn));
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

