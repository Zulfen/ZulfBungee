package tk.zulfengaming.bungeesk.bungeecord.socket;


import net.md_5.bungee.api.config.ServerInfo;
import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Server implements Runnable {
    // plugin instance !!!

    private final BungeeSkProxy pluginInstance;

    // setting up the server
    private ServerSocket serverSocket;
    private final int port;
    private final InetAddress address;
    private int timeout = 10000;

    private boolean running;

    // hey, keep that to yourself!
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
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port, 50, address);

        pluginInstance.log("Server socket successfully established on port " + port + "!");
        pluginInstance.log("Waiting for connections...");

        running = true;
    }

    public void run() {
        // making the server socket

        try {

            start();

            while (running) {
                socket = serverSocket.accept();

                if (isValidClient(socket.getRemoteSocketAddress())) {
                    acceptConnection();
                } else {
                    pluginInstance.warning("Client who tried to connect is not defined in bungeecord's config. Ignoring.");
                    socket.close();
                }
            }

        } catch (IOException serverError) {

            pluginInstance.error("Server encountered an error!");
            serverError.printStackTrace();

            restart();

        }

    }

    public void acceptConnection() throws IOException {

        socket.setTcpNoDelay(true);
        socket.setSoTimeout(timeout);

        ServerConnection connection = new ServerConnection(this);
        SocketAddress connectionAddress = connection.getAddress();
        UUID identifier = UUID.randomUUID();

        pluginInstance.getTaskManager().newTask(connection, String.valueOf(identifier));
        activeConnections.put(connectionAddress, connection);

        pluginInstance.log("Connection established with address: " + connectionAddress);

    }

    public boolean isValidClient(SocketAddress addressIn) {
        Map<String, ServerInfo> servers = pluginInstance.getProxy().getServersCopy();

        for (ServerInfo server : servers.values()) {
            if (addressIn == server.getSocketAddress()) {
                return true;
            }
        }

        return false;
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
        activeConnections.remove(connection.address);
    }

    public ServerConnection getConnection(SocketAddress addressIn) {
        return activeConnections.get(addressIn);
    }

    public void end() throws IOException {

        pluginInstance.log("Shutting down MainServer...");

        for (ServerConnection connection : activeConnections.values()) {
            connection.disconnect();
        }

        running = false;

        socket.close();

    }

    public void restart() {

        pluginInstance.log("Restarting server...");

        try {
            end();
            start();

        } catch (IOException e) {
            pluginInstance.error("Something went wrong trying to restart. You are utterly fucked, i'm sorry.");
            e.printStackTrace();
        }

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

