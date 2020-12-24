package tk.zulfengaming.bungeesk.bungeecord.socket;


import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;
import tk.zulfengaming.bungeesk.universal.exceptions.TaskAlreadyExists;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.HashMap;

public class Server implements Runnable {
    // plugin instance !!!

    public BungeeSkProxy instance;

    // setting up the server
    public int port;
    public InetAddress address;
    public int timeout = 10000;

    public boolean running = true;

    // hey, keep that to yourself!
    public Socket socket;

    // keeping track
    public HashMap<SocketAddress, ServerConnection> activeConnections;

    // quite neat
    PacketHandlerManager packetManager;

    public Server(int port, InetAddress address, BungeeSkProxy instanceIn) {
        this.address = address;
        this.port = port;
        this.instance = instanceIn;

        this.packetManager = new PacketHandlerManager(this);
    }

    public void run() {
        // making the server socket
        try {

            while (running) {
                runServer();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runServer() throws IOException {

        try {

            ServerSocket serverSocket = new ServerSocket(port, 50, address);

            instance.log("Server socket successfully established on port " + port + "!");
            instance.log("Waiting for connections...");

            socket = serverSocket.accept();

            ServerConnection connection = new ServerConnection(this);
            SocketAddress connectionAddress = connection.address;

            instance.taskManager.newTask(connection, String.valueOf(connectionAddress));
            activeConnections.put(connectionAddress, connection);

            instance.log("Connection established with address: " + connectionAddress);

        } catch (TaskAlreadyExists taskAlreadyExists) {
            taskAlreadyExists.printStackTrace();
        }

    }

    public void sendToAllClients(Packet packetIn) {
        instance.log("Sending packet " + packetIn.type.toString() + "to all clients...");

        for (ServerConnection connection : activeConnections.values()) {
            connection.send(packetIn);
        }
    }

    public ServerConnection getConnection(SocketAddress addressIn) {
        final ServerConnection connection = activeConnections.get(addressIn);
        return connection;
    }
}

