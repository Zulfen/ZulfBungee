package tk.zulfengaming.zulfbungee.bungeecord.socket;


import com.google.common.collect.HashBiMap;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.config.ServerInfo;
import tk.zulfengaming.zulfbungee.bungeecord.ZulfBungeecord;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.StorageImpl;
import tk.zulfengaming.zulfbungee.bungeecord.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.bungeecord.storage.db.H2Handler;
import tk.zulfengaming.zulfbungee.bungeecord.storage.db.MySQLHandler;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.ScriptAction;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.nio.file.Path;
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

    private final ArrayList<BaseServerConnection> socketConnections = new ArrayList<>();

    private final HashBiMap<String, BaseServerConnection> activeConnections = HashBiMap.create();

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

            instanceIn.getTaskManager().newTask(() -> {
               storage.initialise();
               storage.setupDatabase();
            });

            pluginInstance.logDebug(ChatColor.GREEN + "Currently using StorageImpl: " + storage.getClass().toString());

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

                        acceptConnection(socket);

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

                        break;
                    }

                    serverSocketAvailable = true;

                    pluginInstance.logInfo(ChatColor.GREEN + "Waiting for connections on " + hostAddress + ":" + port);

                }

            } catch (SocketException | EOFException ignored) {

            } catch (IOException e) {
                pluginInstance.error("An error occurred while running the server!");
                pluginInstance.error("Please report this error on GitHub or directly to the devs:");
                pluginInstance.error("https://github.com/Zulfen/ZulfBungee/issues");
                pluginInstance.error("");

                e.printStackTrace();

                break;

            }

        } while (running);
    }

    private void acceptConnection(Socket socketIn) throws IOException {

        BaseServerConnection connection = new BaseServerConnection(this, socketIn);

        pluginInstance.getTaskManager().newTask(connection);
        addServerConnection(connection);

        pluginInstance.logInfo(ChatColor.GREEN + "Connection established with address: " + connection.getAddress());

    }

    // TODO: Add address whitelist
    private boolean isValidClient(SocketAddress addressIn) {

        Map<String, ServerInfo> servers = pluginInstance.getProxy().getServersCopy();

        boolean portWhitelistEnabled = pluginInstance.getConfig().getBoolean("port-whitelist");
        List<Integer> ports = pluginInstance.getConfig().getIntList("ports");

        for (ServerInfo server : servers.values()) {
            InetSocketAddress inetServerAddr = (InetSocketAddress) server.getSocketAddress();
            InetSocketAddress inetAddrIn = (InetSocketAddress) addressIn;

            if (inetServerAddr.getAddress().equals(inetAddrIn.getAddress())) {

                if (portWhitelistEnabled) {

                    return ports.contains(inetAddrIn.getPort());

                }

                return true;

            }

        }

        return false;
    }

    public void sendToAllClients(Packet packetIn) {
        pluginInstance.logDebug("Sending packet " + packetIn.getType().toString() + " to all clients...");

        for (BaseServerConnection connection : socketConnections) {
            connection.send(packetIn);
        }
    }

    public void syncScriptsFolder(Map<String, ScriptAction> scriptNamesIn) {

        for (BaseServerConnection connection : socketConnections) {

            for (Map.Entry<String, ScriptAction> script : scriptNamesIn.entrySet()) {

                Path scriptPath = pluginInstance.getConfig().getScriptPath(script.getKey());

                connection.sendScript(scriptPath, script.getValue());

            }

        }

    }

    private void addServerConnection(BaseServerConnection connection) {
        socketConnections.add(connection);
    }

    public void addActiveConnection(BaseServerConnection connection, String name) {

        activeConnections.put(name, connection);

        pluginInstance.logDebug("Server '" + name + "' added to the list of active connections!");

        sendToAllClients(new Packet(PacketTypes.PROXY_CLIENT_INFO, false, true, getProxyServerArray()));

    }

    public void removeServerConnection(BaseServerConnection connection) {

        String name = activeConnections.inverse().get(connection);

        pluginInstance.logInfo(String.format(ChatColor.YELLOW + "Disconnecting client %s (%s)", connection.getAddress(), name));

        socketConnections.remove(connection);
        activeConnections.remove(name);

        sendToAllClients(new Packet(PacketTypes.PROXY_CLIENT_INFO, false, true, getProxyServerArray()));

    }

    public void end() throws IOException {

        running = false;

        for (BaseServerConnection connection : socketConnections) {
            connection.shutdown();
        }

        activeConnections.clear();
        socketConnections.clear();

        if (socket != null) {
            socket.close();
        }

        serverSocket.close();

        if (storage != null) {
            storage.shutdown();
        }

    }

    private Optional<StorageImpl> setupStorage() {

        StorageImpl newStorage = null;

        String storageChoice = pluginInstance.getConfig().getString("storage-type");

        if (storageChoice.matches("(?i)mysql")) {
            newStorage = new MySQLHandler(this);
        } else if (storageChoice.matches("(?i)h2")) {
            newStorage = new H2Handler(this);
        }

        return Optional.ofNullable(newStorage);


    }

    public Set<String> getServerNames() {
        return activeConnections.keySet();
    }

    public BaseServerConnection getFromName(String name) {
        return activeConnections.get(name);
    }

    public ProxyServer[] getProxyServerArray() {
        return activeConnections.entrySet().stream()
                .map(proxyServerList -> new ProxyServer(proxyServerList.getKey(), proxyServerList.getValue().getClientInfo()))
                .toArray(ProxyServer[]::new);
    }


    public PacketHandlerManager getPacketManager() {
        return packetManager;
    }

    public Optional<StorageImpl> getStorage() {
        return Optional.ofNullable(storage);
    }

    public ZulfBungeecord getPluginInstance() {
        return pluginInstance;
    }

}

