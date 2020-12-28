package tk.zulfengaming.bungeesk.bungeecord.socket;


import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;
import tk.zulfengaming.bungeesk.universal.exceptions.TaskAlreadyExists;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.net.*;
import java.util.HashMap;

public class Server implements Runnable {
    // plugin instance !!!

    public BungeeSkProxy pluginInstance;

    // setting up the server
    public ServerSocket serverSocket;
    public int port;
    public InetAddress address;
    public int timeout = 10000;

    public boolean running = true;

    // hey, keep that to yourself!
    public Socket socket;
    public SocketAddress socketAddress;

    // keeping track
    public HashMap<SocketAddress, ServerConnection> activeConnections;

    // quite neat
    PacketHandlerManager packetManager;

    public Server(int port, InetAddress address, BungeeSkProxy instanceIn) {
        this.address = address;
        this.socketAddress = new InetSocketAddress(this.address, this.port);
        this.port = port;
        this.pluginInstance = instanceIn;

        this.packetManager = new PacketHandlerManager(this);
    }

    public void run() {
        // making the server socket

        try {

            serverSocket = new ServerSocket(port, 50, address);

            pluginInstance.log("Server socket successfully established on port " + port + "!");
            pluginInstance.log("Waiting for connections...");

            while (running) {
                socket = serverSocket.accept();
                acceptConnection();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void acceptConnection() throws IOException {

        try {

            ServerConnection connection = new ServerConnection(this);
            SocketAddress connectionAddress = connection.address;

            pluginInstance.taskManager.newTask(connection, String.valueOf(connectionAddress));
            activeConnections.put(connectionAddress, connection);

            pluginInstance.log("Connection established with address: " + connectionAddress);

        } catch (TaskAlreadyExists taskAlreadyExists) {
            taskAlreadyExists.printStackTrace();
        }

    }

    public void sendToAllClients(Packet packetIn) {
        pluginInstance.log("Sending packet " + packetIn.type.toString() + "to all clients...");

        for (ServerConnection connection : activeConnections.values()) {
            connection.send(packetIn);
        }
    }

    public ServerConnection getConnection(SocketAddress addressIn) {
        final ServerConnection connection = activeConnections.get(addressIn);
        return connection;
    }
}

