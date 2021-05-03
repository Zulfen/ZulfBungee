package tk.zulfengaming.bungeesk.bungeecord.socket;


import net.md_5.bungee.api.config.ServerInfo;
import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;
import tk.zulfengaming.bungeesk.bungeecord.handlers.SocketHandler;
import tk.zulfengaming.bungeesk.bungeecord.interfaces.PacketHandlerManager;
import tk.zulfengaming.bungeesk.bungeecord.interfaces.StorageImpl;
import tk.zulfengaming.bungeesk.bungeecord.storage.MySQLHandler;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Server implements Runnable {
    // plugin instance !!!

    private final BungeeSkProxy pluginInstance;
    private final SocketHandler socketHandler;

    // setting up the server
    private final int port;
    private final InetAddress hostAddress;

    private boolean running = true;
    private boolean serverSocketAvailable = false;

    // hey, keep that to yourself!
    private ServerSocket serverSocket;
    private Socket socket;

    // keeping track
    private final HashMap<SocketAddress, ServerConnection> socketConnections = new HashMap<>();

    private final HashMap<ServerConnection, String> activeConnections = new HashMap<>();

    // quite neat
    private final PacketHandlerManager packetManager;

    // storage
    private StorageImpl storage;

    public Server(int port, InetAddress address, BungeeSkProxy instanceIn) {
        this.hostAddress = address;
        this.port = port;
        this.pluginInstance = instanceIn;

        this.packetManager = new PacketHandlerManager(this);
        this.socketHandler = new SocketHandler(this);

        setupStorage();

        pluginInstance.getTaskManager().newTask(() -> storage.initialise(), "StorageSetupThread");
    }

    private Future<Optional<ServerSocket>> start() throws IOException {
        return pluginInstance.getTaskManager().getExecutorService().submit(socketHandler);
    }

    public void run() {
        // making the server socket

        do {
            try {

                if (serverSocketAvailable) {
                    socket = serverSocket.accept();

                    if (isValidClient(socket.getRemoteSocketAddress())) {
                        acceptConnection();

                    } else {
                        pluginInstance.warning("Client who tried to connect is not defined in bungeecord's config. Ignoring.");

                        socket.close();
                    }

                } else {

                    for (ServerConnection connection : socketConnections.values()) {
                        connection.end();
                    }

                    Optional<ServerSocket> futureSocket = start().get(5, TimeUnit.SECONDS);

                    pluginInstance.log("Establishing a socket...");

                    if (futureSocket.isPresent()) {

                        serverSocket = futureSocket.get();
                        pluginInstance.log("Waiting for connections on " + hostAddress + ":" + port);

                        serverSocketAvailable = true;

                    }

                }

            } catch (SocketException e) {
                serverSocketAvailable = false;

            } catch (EOFException | InterruptedException ignored) {

            } catch (IOException | TimeoutException | ExecutionException serverError) {

                try {
                    serverSocket.close();

                } catch (IOException e) {
                    pluginInstance.error("Couldn't close ServerSocket");
                    e.printStackTrace();
                }

                pluginInstance.error("Server encountered an error!");
                serverError.printStackTrace();

                serverSocketAvailable = false;

            }

        } while (running);
    }

    private void acceptConnection() throws IOException {

        UUID identifier = UUID.randomUUID();

        ServerConnection connection = new ServerConnection(this, identifier.toString());
        SocketAddress connectionAddress = connection.getAddress();

        pluginInstance.getTaskManager().newTask(connection, String.valueOf(identifier));
        addSocketConnection(connectionAddress, connection);

        pluginInstance.log("Connection established with address: " + connectionAddress);

    }

    public boolean isValidClient(SocketAddress addressIn) {
        Map<String, ServerInfo> servers = pluginInstance.getProxy().getServersCopy();

        for (ServerInfo server : servers.values()) {
            InetSocketAddress serverAddress = (InetSocketAddress) server.getSocketAddress();
            InetSocketAddress inetAddressIn = (InetSocketAddress) addressIn;

            if (serverAddress.getAddress().equals(inetAddressIn.getAddress())) {
                return true;
            }

        }

        return false;
    }

    public void sendToAllClients(Packet packetIn) {
        pluginInstance.log("Sending packet " + packetIn.getType().toString() + "to all clients...");

        for (ServerConnection connection : socketConnections.values()) {
            connection.send(packetIn);
        }
    }

    private void addSocketConnection(SocketAddress addressIn, ServerConnection connection) {
        socketConnections.put(addressIn, connection);
    }

    public void addActiveConnection(ServerConnection connection, String name) {
        activeConnections.put(connection, name);
    }

    public void removeSocketConnection(ServerConnection connection) {
        socketConnections.remove(connection.getAddress());

        activeConnections.remove(connection);

        try {

            connection.getSocket().close();

        } catch (IOException e) {
            pluginInstance.error("Error removing socket connection:");

            e.printStackTrace();
        }
    }

    public ServerConnection getSocketConnection(SocketAddress addressIn) {
        return socketConnections.get(addressIn);
    }

    public void end() throws IOException {

        running = false;
        serverSocketAvailable = false;

        for (ServerConnection connection : socketConnections.values()) {
            connection.end();
        }

        serverSocket.close();
        storage.shutdown();

    }

    private void setupStorage() {

        storage = new MySQLHandler(this);

    }

    public int getPort() {
        return port;
    }

    public InetAddress getHostAddress() {
        return hostAddress;
    }

    public boolean isRunning() {
        return running;
    }

    public Socket getSocket() {
        return socket;
    }

    public PacketHandlerManager getPacketManager() {
        return packetManager;
    }

    public StorageImpl getStorage() {
        return storage;
    }

    public Collection<ServerConnection> getSocketConnections() {
        return socketConnections.values();
    }

    public Set<ServerConnection> getActiveConnections() {
        return activeConnections.keySet();
    }

    public BungeeSkProxy getPluginInstance() {
        return pluginInstance;
    }

}

