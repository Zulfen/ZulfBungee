package tk.zulfengaming.bungeesk.bungeecord.socket;


import net.md_5.bungee.api.scheduler.ScheduledTask;
import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;
import tk.zulfengaming.bungeesk.universal.socket.PacketHandlerManager;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import static tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot.*;

public class Server implements Runnable {
    // plugin instance !!!

    BungeeSkProxy instance;

    // setting up the server
    public int port;
    public InetAddress address;

    // hey, keep that to yourself!
    Socket socket;

    // keeping track
    public ArrayList<ServerConnection> activeConnections;

    // quite neat
    PacketHandlerManager packetManager;

    public Server(int port, InetAddress address, BungeeSkProxy instanceIn) {
        this.address = address;
        this.port = port;
        this.instance = instanceIn;

        this.packetManager = new PacketHandlerManager();
    }

    public void start() {

    }

    public void run() {
        // making the server socket
        try {
            ServerSocket serverSocket = new ServerSocket(port, 0, address);
            log("Server socket successfully established on port " + port + "!");

            socket = serverSocket.accept();

            ScheduledTask connection = instance.scheduler.runAsync(instance, new ServerConnection(this));

        } catch (IOException e) {
            log("There was an error trying to create the server socket on port " + port);
            e.printStackTrace();
        }
    }
}

