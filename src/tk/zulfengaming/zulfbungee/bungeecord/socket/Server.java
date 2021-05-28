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
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

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

                    SocketAddress remoteAddress = socket.getRemoteSocketAddress();

                    if (isValidClient(remoteAddress)) {

                        acceptConnection();

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

    private boolean isValidClient(SocketAddress addressIn) {
        Map<String, ServerInfo> servers = pluginInstance.getProxy().getServersCopy();

        final boolean portWhitelistEnabled = pluginInstance.getConfig().getBoolean("port-whitelist");
        List<Integer> ports = pluginInstance.getConfig().getIntList("ports");

        for (ServerInfo server : servers.values()) {
            final InetSocketAddress inetServerAddr = (InetSocketAddress) server.getSocketAddress();
            final InetSocketAddress inetAddrIn = (InetSocketAddress) addressIn;

            if (inetServerAddr.getAddress().equals(inetAddrIn.getAddress())) {

                if (portWhitelistEnabled) {

                    return ports.contains(inetAddrIn.getPort());

                }

                return true;

            }

            return false;

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

            connection.send(new Packet(PacketTypes.INVALID_CONFIGURATION, false, true, null));

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

