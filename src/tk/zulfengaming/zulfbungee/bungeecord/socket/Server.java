package tk.zulfengaming.zulfbungee.bungeecord.socket;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import tk.zulfengaming.zulfbungee.bungeecord.ZulfBungeecord;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.StorageImpl;
import tk.zulfengaming.zulfbungee.bungeecord.storage.MySQLHandler;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;

import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.*;

public class Server implements Runnable {
    // plugin instance !!!

    private final ZulfBungeecord pluginInstance;

    // setting up the server
    private final int port;
    private final InetAddress hostAddress;

    private boolean running = true;
    private boolean serverSocketAvailable = false;

    // hey, keep that to yourself!
    private ServerSocket serverSocket;
    private Socket socket;

    // keeping track
    private final BiMap<SocketAddress, ServerConnection> serverConnections = HashBiMap.create();

    private final BiMap<String, ServerConnection> activeConnections = HashBiMap.create();

    private final ArrayList<SocketAddress> invalidClients = new ArrayList<>();

    // quite neat
    private final PacketHandlerManager packetManager;

    // storage
    private StorageImpl storage;

    public Server(int port, InetAddress address, ZulfBungeecord instanceIn) {
        this.hostAddress = address;
        this.port = port;
        this.pluginInstance = instanceIn;

        this.packetManager = new PacketHandlerManager(this);

        Optional<StorageImpl> newStorage = setupStorage();

        if (newStorage.isPresent()) {

            storage = newStorage.get();

            instanceIn.getTaskManager().newTask(() -> storage.initialise(), "StorageSetupThread");

        }

    }


    public void run() {
        // making the server socket

        do {
            try {

                if (serverSocketAvailable) {
                    socket = serverSocket.accept();

                    SocketAddress connectedAddress = socket.getRemoteSocketAddress();

                    if (isValidClient(connectedAddress)) {
                        acceptConnection();

                    } else {

                        if (!invalidClients.contains(connectedAddress)) {
                            pluginInstance.warning("Client who tried to connect is not defined in bungeecord's config. Ignoring.");
                        }

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

                        running = false;
                        break;
                    }

                    serverSocketAvailable = true;

                    pluginInstance.logInfo(ChatColor.GREEN + "Waiting for connections on " + hostAddress + ":" + port);

                }

            } catch (SocketException | EOFException ignored) {

            } catch (IOException e) {
                pluginInstance.error("An error occurred while running the server!");
                pluginInstance.error("Please report this error on GitHub or directly to the devs:");
                pluginInstance.error("(insert url)");
                pluginInstance.error("");

                e.printStackTrace();

            }

        } while (running);
    }

    private void acceptConnection() throws IOException {

        UUID identifier = UUID.randomUUID();

        ServerConnection connection = new ServerConnection(this, identifier.toString());
        SocketAddress connectionAddress = connection.getAddress();

        pluginInstance.getTaskManager().newTask(connection, String.valueOf(identifier));
        addServerConnection(connectionAddress, connection);

        pluginInstance.logInfo(ChatColor.GREEN + "Connection established with address: " + connectionAddress);

    }

    public boolean isValidClient(SocketAddress addressIn) {
        Map<String, ServerInfo> servers = pluginInstance.getProxy().getServersCopy();

        for (ServerInfo server : servers.values()) {
            final InetSocketAddress serverAddress = (InetSocketAddress) server.getSocketAddress();
            final InetSocketAddress inetAddressIn = (InetSocketAddress) addressIn;

            if (serverAddress.getAddress().equals(inetAddressIn.getAddress()) && !invalidClients.contains(addressIn)) {
                return true;
            }

        }

        return false;
    }

    public void sendToAllClients(Packet packetIn) {
        pluginInstance.logDebug("Sending packet " + packetIn.getType().toString() + " to all clients...");

        for (ServerConnection connection : activeConnections.values()) {
            connection.send(packetIn);
        }
    }

    private void addServerConnection(SocketAddress addressIn, ServerConnection connection) {
        serverConnections.put(addressIn, connection);
    }

    public void addActiveConnection(ServerConnection connection, String name) {

        SocketAddress address = connection.getAddress();

        if (!activeConnections.containsKey(name)) {

            invalidClients.remove(address);

            activeConnections.put(name, connection);

            pluginInstance.logDebug("Server '" + name + "' added to the list of active connections!");


        } else {

            pluginInstance.warning("The server that just tried to connect at " + connection.getAddress().toString() +
                    " is set to a name that is already taken! Please change it!");

            invalidClients.add(connection.getAddress());
            connection.end();

        }

    }

    public void removeServerConnection(ServerConnection connection) {

        serverConnections.remove(connection.getAddress());

        activeConnections.remove(activeConnections.inverse().get(connection));

    }

    public void end() throws IOException {

        running = false;

        for (ServerConnection connection : serverConnections.values()) {
            connection.shutdown();
        }

        activeConnections.clear();
        serverConnections.clear();
        invalidClients.clear();

        if (socket != null) {
            socket.close();
        }

        serverSocket.close();

        storage.shutdown();

    }

    private Optional<StorageImpl> setupStorage() {

        StorageImpl newStorage = null;

        String storageChoice = pluginInstance.getConfig().getString("storage-type");

        if (storageChoice.matches("(?i)mysql")) {
            newStorage = new MySQLHandler(this);
        }

        return Optional.ofNullable(newStorage);


    }

    public Socket getSocket() {
        return socket;
    }

    public PacketHandlerManager getPacketManager() {
        return packetManager;
    }

    public Optional<StorageImpl> getStorage() {
        return Optional.ofNullable(storage);
    }

    public BiMap<String, ServerConnection> getActiveConnections() {
        return activeConnections;
    }

    public BiMap<SocketAddress, ServerConnection> getServerConnections() {
        return serverConnections;
    }

    public ZulfBungeecord getPluginInstance() {
        return pluginInstance;
    }

}

