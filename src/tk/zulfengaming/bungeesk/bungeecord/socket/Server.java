package tk.zulfengaming.bungeesk.bungeecord.socket;


import net.md_5.bungee.api.config.ServerInfo;
import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;
import tk.zulfengaming.bungeesk.bungeecord.handlers.SocketHandler;
import tk.zulfengaming.bungeesk.bungeecord.interfaces.PacketHandlerManager;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
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
    private final HashMap<SocketAddress, ServerConnection> activeConnections = new HashMap<>();

    // quite neat
    private final PacketHandlerManager packetManager;

    public Server(int port, InetAddress address, BungeeSkProxy instanceIn) {
        this.hostAddress = address;
        this.port = port;
        this.pluginInstance = instanceIn;

        this.packetManager = new PacketHandlerManager(this);
        this.socketHandler = new SocketHandler(this);
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

                    for (ServerConnection connection : activeConnections.values()) {
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

            } catch (IOException | TimeoutException | ExecutionException serverError) {

                pluginInstance.error("Server encountered an error!");
                serverError.printStackTrace();

                serverSocketAvailable = false;

            } catch (InterruptedException ignored) {

            }

        } while (running);
    }

    public void acceptConnection() throws IOException {

        UUID identifier = UUID.randomUUID();

        ServerConnection connection = new ServerConnection(this, identifier.toString());
        SocketAddress connectionAddress = connection.getAddress();

        pluginInstance.getTaskManager().newTask(connection, String.valueOf(identifier));
        activeConnections.put(connectionAddress, connection);

        pluginInstance.log("Connection established with address: " + connectionAddress);

    }

    public boolean isValidClient(SocketAddress addressIn) {
        Map<String, ServerInfo> servers = pluginInstance.getProxy().getServersCopy();

        for (ServerInfo server : servers.values()) {
            InetSocketAddress serverAddress = (InetSocketAddress) server.getSocketAddress();
            InetSocketAddress inetAddressIn = (InetSocketAddress) addressIn;

            pluginInstance.log(serverAddress.toString() + " / " +  inetAddressIn.toString());

            if (serverAddress.equals(inetAddressIn)) {
                pluginInstance.log("The same!");
                return true;
            }

        }

        return true;
    }

    public void sendToAllClients(Packet packetIn) {
        pluginInstance.log("Sending packet " + packetIn.getType().toString() + "to all clients...");

        for (ServerConnection connection : activeConnections.values()) {
            connection.send(packetIn);
        }
    }

    public void addConnection(SocketAddress addressIn, ServerConnection connection) {
        activeConnections.put(addressIn, connection);
    }

    public void removeConnection(ServerConnection connection) {
        activeConnections.remove(connection.getAddress());
    }

    public ServerConnection getConnection(SocketAddress addressIn) {
        return activeConnections.get(addressIn);
    }

    public void end() throws IOException {

        serverSocketAvailable = false;
        running = false;

        for (ServerConnection connection : activeConnections.values()) {
            connection.end();
        }

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

    public BungeeSkProxy getPluginInstance() {
        return pluginInstance;
    }

}

