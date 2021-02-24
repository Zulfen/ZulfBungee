package tk.zulfengaming.bungeesk.bungeecord.socket;


import net.md_5.bungee.api.scheduler.ScheduledTask;
import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;
import tk.zulfengaming.bungeesk.bungeecord.handlers.SocketHandler;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;
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
    private final InetAddress address;

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
        this.address = address;
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
                    pluginInstance.log("poop shit: before accept");
                    socket = serverSocket.accept();
                    pluginInstance.log("poop shit: middle accept");

                    if (isValidClient(socket.getRemoteSocketAddress())) {
                        acceptConnection();
                        pluginInstance.log("poop shit: after accept");
                    } else {
                        pluginInstance.warning("Client who tried to connect is not defined in bungeecord's config. Ignoring.");
                        socket.close();
                    }

                } else {

                    for (ServerConnection connection : activeConnections.values()) {
                        connection.disconnect();
                    }

                    Optional<ServerSocket> futureSocket = start().get(5, TimeUnit.SECONDS);

                    pluginInstance.log("Establishing a socket...");

                    if (futureSocket.isPresent()) {

                        serverSocket = futureSocket.get();
                        pluginInstance.log("Waiting for connections on " + address + ":" + port);

                        serverSocketAvailable = true;

                    }

                }

            } catch (IOException | InterruptedException | TimeoutException | ExecutionException serverError) {

                pluginInstance.error("Server encountered an error!");
                serverError.printStackTrace();

                serverSocketAvailable = false;

            }
        } while (running);
    }

    public void acceptConnection() throws IOException {

        UUID identifier = UUID.randomUUID();

        ServerConnection connection = new ServerConnection(this, identifier.toString());
        SocketAddress connectionAddress = connection.getAddress();

        ScheduledTask connectionTask = pluginInstance.getTaskManager().newTask(connection, String.valueOf(identifier));
        activeConnections.put(connectionAddress, connection);

        pluginInstance.log("Connection established with address: " + connectionAddress);

    }

    public boolean isValidClient(SocketAddress addressIn) {
//        Map<String, ServerInfo> servers = pluginInstance.getProxy().getServersCopy();
//
//        for (ServerInfo server : servers.values()) {
//            if (addressIn == server.getSocketAddress()) {
//                return true;
//            }
//        }

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

        for (ServerConnection connection : activeConnections.values()) {
            connection.disconnect();
        }

        running = false;

        socket.close();

    }

    public int getPort() {
        return port;
    }

    public InetAddress getAddress() {
        return address;
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

