package tk.zulfengaming.zulfbungee.universal.managers;


import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.command.util.ChatColour;
import tk.zulfengaming.zulfbungee.universal.socket.ProxyServerConnection;
import tk.zulfengaming.zulfbungee.universal.interfaces.StorageImpl;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientInfo;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptAction;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.EventPacket;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.storage.db.H2Impl;
import tk.zulfengaming.zulfbungee.universal.storage.db.MySQLImpl;

import java.io.IOException;
import java.net.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class MainServer<P, T> {

    protected final ZulfBungeeProxy<P, T> pluginInstance;

    protected final CopyOnWriteArrayList<ProxyServerConnection<P, T>> connections = new CopyOnWriteArrayList<>();

    protected final ConcurrentHashMap<SocketAddress, String> addressNames = new ConcurrentHashMap<>();
    protected final ConcurrentHashMap<String, ProxyServerConnection<P, T>> activeConnections = new ConcurrentHashMap<>();

    protected final ConcurrentHashMap<String, ClientInfo> clientInfos = new ConcurrentHashMap<>();

    protected final ConcurrentLinkedQueue<EventPacket> unsentEventPackets = new ConcurrentLinkedQueue<>();

    protected final ProxyTaskManager taskManager;

    // storage
    private volatile StorageImpl<P, T> storage;

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

    // this method is usually used for announcing state changes or events, so we should keep track of
    // unsent packets and send them when a connection is available.
    public void sendDirectToAll(Packet packetIn) {
        pluginInstance.logDebug("Sending packet " + packetIn.getType().toString() + " to all clients...");
        if (!connections.isEmpty()) {
            for (ProxyServerConnection<P, T> connection : connections) {
                connection.sendDirect(packetIn);
            }
        } else if (packetIn instanceof EventPacket) {
            unsentEventPackets.offer((EventPacket) packetIn);
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
        addressNames.put(address, name);
        activeConnections.put(name, connectionIn);
        clientInfos.put(name, infoIn);

        pluginInstance.logInfo(String.format("%sConnection established with %s (%s)", ChatColour.GREEN, address, name));
        sendDirectToAll(new Packet(PacketTypes.PROXY_CLIENT_INFO, false, true, getClientServerArray()));

    }

    public void removeServerConnection(String name, SocketAddress address) {
        activeConnections.remove(name);
        clientInfos.remove(name);
        pluginInstance.logInfo(String.format(ChatColour.YELLOW + "Disconnecting client %s (%s)", address, name));
        sendDirectToAll(new Packet(PacketTypes.PROXY_CLIENT_INFO, false, true, getClientServerArray()));
    }

    public void removeServerConnection(ProxyServerConnection<P, T> connectionIn) {

        SocketAddress socketAddress = connectionIn.getAddress();
        connections.remove(connectionIn);
        String name = addressNames.remove(socketAddress);

        if (name != null) {
            removeServerConnection(name, socketAddress);
        } else {
            pluginInstance.logInfo(String.format(ChatColour.YELLOW + "Disconnecting client %s", socketAddress));
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

    public Optional<ClientPlayer> toClientPlayer(ZulfProxyPlayer<P, T> proxyPlayerIn) {

        String serverName = proxyPlayerIn.getServer().getName();
        ClientInfo clientInfo = clientInfos.get(serverName);

        if (clientInfo != null) {
            ClientServer clientServer = new ClientServer(serverName, clientInfo);
            Optional<InetSocketAddress> optionalVirtHost = proxyPlayerIn.getVirtualHost();
            return optionalVirtHost.map(inetSocketAddress
                    -> Optional.of(new ClientPlayer(proxyPlayerIn.getName(), proxyPlayerIn.getUuid(), clientServer, inetSocketAddress))).orElseGet(()
                    -> Optional.of(new ClientPlayer(proxyPlayerIn.getName(), proxyPlayerIn.getUuid(), clientServer)));
        }

        return Optional.empty();

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

    public boolean remainingEventPackets() {
        return unsentEventPackets.peek() != null;
    }

    public EventPacket pollEventPacket() {
        return unsentEventPackets.poll();
    }

    public Optional<StorageImpl<P, T>> getStorage() {
        return Optional.ofNullable(storage);
    }

    public ZulfBungeeProxy<P, T> getPluginInstance() {
        return pluginInstance;
    }

}

