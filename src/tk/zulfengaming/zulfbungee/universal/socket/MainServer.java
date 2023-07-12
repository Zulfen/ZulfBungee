package tk.zulfengaming.zulfbungee.universal.socket;


import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.command.util.ChatColour;
import tk.zulfengaming.zulfbungee.universal.handlers.socket.ProxyChannelCommHandler;
import tk.zulfengaming.zulfbungee.universal.interfaces.MessageCallback;
import tk.zulfengaming.zulfbungee.universal.interfaces.ProxyCommHandler;
import tk.zulfengaming.zulfbungee.universal.interfaces.ProxyServerConnection;
import tk.zulfengaming.zulfbungee.universal.interfaces.StorageImpl;
import tk.zulfengaming.zulfbungee.universal.managers.ProxyTaskManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientInfo;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptAction;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import tk.zulfengaming.zulfbungee.universal.storage.db.H2Impl;
import tk.zulfengaming.zulfbungee.universal.storage.db.MySQLImpl;

import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainServer<P, T> {
    // plugin instance !!!

    protected final ZulfBungeeProxy<P, T> pluginInstance;

    protected final CopyOnWriteArrayList<ProxyServerConnection<P, T>> connections = new CopyOnWriteArrayList<>();

    protected final ConcurrentHashMap<SocketAddress, String> addressNames = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, ProxyServerConnection<P, T>> activeConnections = new ConcurrentHashMap<>();

    protected final ConcurrentHashMap<String, ClientInfo> clientInfos = new ConcurrentHashMap<>();

    protected final ProxyTaskManager taskManager;

    // storage
    private StorageImpl<P, T> storage;

    public MainServer(ZulfBungeeProxy<P, T> instanceIn) {

        this.pluginInstance = instanceIn;
        this.taskManager = instanceIn.getTaskManager();

        pluginInstance.getTaskManager().newTask(() -> {

            Optional<StorageImpl<P, T>> newStorage = setupStorage();

            if (newStorage.isPresent()) {

                storage = newStorage.get();
                storage.setupDatabase();

                pluginInstance.logDebug(ChatColour.GREEN + "Currently using StorageImpl: " + storage.getClass().toString());

            }

        });

        pluginInstance.getUpdater().checkUpdate(pluginInstance.getConsole(), true);

    }

    protected void startConnection(ProxyServerConnection<P, T> connectionIn) {
        taskManager.newTask(connectionIn);
        connections.add(connectionIn);
    }


    public void sendDirectToAllAsync(Packet packetIn) {
        taskManager.newTask(() -> sendDirectToAll(packetIn));
    }

    public void sendDirectToAll(Packet packetIn) {
        pluginInstance.logDebug("Sending packet " + packetIn.getType().toString() + " to all clients...");
        for (ProxyServerConnection<P, T> connection : connections) {
            connection.sendDirect(packetIn);
        }
    }

    public void syncScripts(Map<Path, ScriptAction> scriptNamesIn, ProxyCommandSender<P, T> senderIn) {

        for (ProxyServerConnection<P, T> connection : connections) {

            for (Map.Entry<Path, ScriptAction> script : scriptNamesIn.entrySet()) {
                String name = script.getKey().getFileName().toString();
                connection.sendScript(name, script.getKey(), script.getValue(), senderIn);
            }

        }

    }

    public void syncScripts(Map<String, Path> scriptNamesIn, ScriptAction scriptActionIn, ProxyCommandSender<P, T> senderIn) {

        for (ProxyServerConnection<P, T> connection : connections) {

            for (Map.Entry<String, Path> script : scriptNamesIn.entrySet()) {
                connection.sendScript(script.getKey(), script.getValue(), scriptActionIn, senderIn);
            }

        }

    }

    public void addActiveConnection(ProxyServerConnection<P, T> connectionIn, String name, ClientInfo infoIn) {

        SocketAddress address = connectionIn.getAddress();

        if (!activeConnections.containsKey(name)) {

            addressNames.put(address, name);
            activeConnections.put(name, connectionIn);
            clientInfos.put(name, infoIn);

            pluginInstance.logInfo(String.format("%sConnection established with %s (%s)", ChatColour.GREEN, address, name));

            sendDirectToAll(new Packet(PacketTypes.PROXY_CLIENT_INFO, false, true, getClientServerArray()));

        } else {
            pluginInstance.warning(String.format("Server %s is already registered. Please change the forced-connection-name in the client's config to something different!", name));
            connectionIn.sendDirect(new Packet(PacketTypes.INVALID_CONFIGURATION, false, true, new Object[0]));
        }

    }

    public void removeServerConnection(ProxyServerConnection<P, T> connectionIn) {

        connections.remove(connectionIn);
        String name = addressNames.remove(connectionIn.getAddress());

        if (name != null) {

            activeConnections.remove(name);
            clientInfos.remove(name);

            if (connectionIn instanceof ChannelServerConnection) {
                pluginInstance.unregisterMessageChannel(String.format("zproxy:channel:%s", name));
            }

            pluginInstance.logInfo(String.format(ChatColour.YELLOW + "Disconnecting client %s (%s)", connectionIn.getAddress(), name));

            sendDirectToAll(new Packet(PacketTypes.PROXY_CLIENT_INFO, false, true, getClientServerArray()));

        } else {
            pluginInstance.logInfo(String.format(ChatColour.YELLOW + "Disconnecting client %s", connectionIn.getAddress()));
        }


    }


    public void end() throws IOException {

        for (ProxyServerConnection<P, T> connection : connections) {
            connection.destroy();
        }

        if (storage != null) {
            storage.shutdown();
        }

    }

    private Optional<StorageImpl<P, T>> setupStorage() {

        StorageImpl<P, T> newStorage = null;

        String storageChoice = pluginInstance.getConfig().getString("storage-type");

        if (storageChoice.matches("(?i)mysql")) {
            newStorage = new MySQLImpl<>(this);
        } else if (storageChoice.matches("(?i)h2")) {
            newStorage = new H2Impl<>(this);
        }

        return Optional.ofNullable(newStorage);


    }

    public Set<String> getServerNames() {
        return activeConnections.keySet();
    }

    public Optional<ProxyServerConnection<P, T>> getConnection(String name) {
        return Optional.ofNullable(activeConnections.get(name));
    }

    public Optional<ProxyServerConnection<P, T>> getConnection(ClientServer serverIn) {
        return Optional.ofNullable(activeConnections.get(serverIn.getName()));
    }

    public Optional<ProxyServerConnection<P, T>> getConnection(ZulfProxyPlayer<P, T> playerIn) {
        return Optional.ofNullable(activeConnections.get(playerIn.getServer().getName()));
    }


    private ClientServer[] getClientServerArray() {
        return clientInfos.entrySet().stream()
                .map(entry -> new ClientServer(entry.getKey(), entry.getValue()))
                .toArray(ClientServer[]::new);
    }

    public Optional<ClientInfo> getClientInfo(String nameIn) {
        return Optional.ofNullable(clientInfos.get(nameIn));
    }

    public boolean areClientsConnected() {
        return connections.size() > 0;
    }

    public Optional<StorageImpl<P, T>> getStorage() {
        return Optional.ofNullable(storage);
    }

    public ZulfBungeeProxy<P, T> getPluginInstance() {
        return pluginInstance;
    }

}

