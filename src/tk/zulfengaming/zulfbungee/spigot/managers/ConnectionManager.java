package tk.zulfengaming.zulfbungee.spigot.managers;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.registrations.Classes;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.socket.ChannelConnection;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.spigot.socket.SocketConnection;
import tk.zulfengaming.zulfbungee.spigot.tasks.ConnectionTask;
import tk.zulfengaming.zulfbungee.spigot.tasks.GlobalScriptsTask;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientInfo;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.NetworkVariable;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptAction;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptInfo;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.Value;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConnectionManager extends BukkitRunnable {

    private final ZulfBungeeSpigot pluginInstance;
    
    private final CopyOnWriteArrayList<Connection> allConnections = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<SocketAddress> blockedConnections = new CopyOnWriteArrayList<>();

    private final ConcurrentHashMap<SocketAddress, String> addressNames = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Connection> connectionNames = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<String, ClientInfo> proxyServers = new ConcurrentHashMap<>();

    private final List<File> scriptFiles = Collections.synchronizedList(new ArrayList<>());

    private final AtomicBoolean running = new AtomicBoolean(true);

    private final AtomicInteger registered = new AtomicInteger();

    private final LinkedBlockingQueue<Optional<Packet>> connectionPackets = new LinkedBlockingQueue<>();

    private final Semaphore connectionBarrier = new Semaphore(0);
    private final ConnectionTask connectionTask;

    private final TaskManager taskManager;

    // representation of this client as a server.
    private ClientServer thisServer;

    public ConnectionManager(ZulfBungeeSpigot pluginIn, InetAddress serverAddress, int serverPort) {
        this.pluginInstance = pluginIn;
        this.taskManager = pluginInstance.getTaskManager();
        this.connectionTask = new ConnectionTask(this, connectionBarrier, serverAddress, serverPort);
    }

    public ConnectionManager(ZulfBungeeSpigot pluginIn, InetAddress clientAddress, int clientPort, InetAddress serverAddress, int serverPort) {
        this.pluginInstance = pluginIn;
        this.taskManager = pluginInstance.getTaskManager();
        this.connectionTask = new ConnectionTask(this, connectionBarrier, clientAddress, clientPort, serverAddress, serverPort);
    }

    @Override
    public void run() {

        Thread.currentThread().setName("ConnectionManager");
        pluginInstance.warning("Attempting to connect...");

        do {

            try {

                connectionPackets.clear();

                while (registered.get() > 0) {

                    for (Connection connection : allConnections) {

                        try {

                            Optional<Packet> getPacket = connection.read();
                            connectionPackets.put(getPacket);

                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }

                    }

                }

                connectionPackets.put(Optional.empty());

                taskManager.newAsyncTask(connectionTask);
                connectionBarrier.acquire();


            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        } while(running.get());

        connectionPackets.offer(Optional.empty());

    }

    private synchronized Queue<Packet> sendGetPacketList(Packet packetIn) {

        Queue<Packet> packetQueue = new LinkedList<>();

        if (registered.get() > 0) {

            sendDirect(packetIn);
            for (int i = 0; i < allConnections.size(); i++) {
                try {
                    Optional<Packet> take = connectionPackets.take();
                    take.ifPresent(packetQueue::offer);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

        }

        return packetQueue;

    }

    public void sendDirect(Packet packetIn) {
        allConnections.forEach(connection -> connection.sendDirect(packetIn));
    }

    // returns the first packet it gets from any of the connections. keep this in mind.
    public Optional<Packet> send(Packet packetIn) {
        Queue<Packet> queue = sendGetPacketList(packetIn);
        return Optional.ofNullable(queue.poll());
    }

    public List<ClientPlayer> getPlayers(ClientServer[] serversIn) {

        Queue<Packet> packets;

        if (serversIn.length > 0) {
            packets = sendGetPacketList(new Packet(PacketTypes.PROXY_PLAYERS,
                    true, false, serversIn));
        } else {
            packets = sendGetPacketList(new Packet(PacketTypes.PROXY_PLAYERS,
                    true, false, new Object[0]));
        }

        return packets.stream()
                .flatMap(packet -> Arrays.stream(packet.getDataArray()))
                .filter(ClientPlayer.class::isInstance)
                .map(ClientPlayer.class::cast)
                .collect(Collectors.toList());

    }

    public Optional<NetworkVariable> requestNetworkVariable(String nameIn) {

        Optional<Packet> send = send(new Packet(PacketTypes.NETWORK_VARIABLE_GET, true, false, nameIn));

        if (send.isPresent()) {
            Packet packet = send.get();
            if (packet.getDataArray().length > 0) {
                return Optional.of((NetworkVariable) packet.getDataSingle());
            }
        }

        return Optional.empty();

    }

    public void modifyNetworkVariable(Object[] delta, Changer.ChangeMode mode, String variableNameIn) {

        Value[] values = new Value[0];

        if (mode != Changer.ChangeMode.DELETE) {
            values = Stream.of(delta)
                    .map(Classes::serialize)
                    .filter(Objects::nonNull)
                    .map(value -> new Value(value.type, value.data))
                    .toArray(Value[]::new);
        }

        NetworkVariable variableOut = new NetworkVariable(variableNameIn, mode.name(), values);
        sendDirect(new Packet(PacketTypes.NETWORK_VARIABLE_MODIFY, true, false, variableOut));

    }

    public void setProxyServers(Map<String, ClientInfo> serversIn) {

        boolean changed = proxyServers.keySet().retainAll(serversIn.keySet());

        if (!changed) {
            proxyServers.putAll(serversIn);
        }

    }

    public List<ClientServer> getAllProxyServers() {
        return proxyServers.entrySet().stream()
                .map(server -> new ClientServer(server.getKey(), server.getValue()))
                .collect(Collectors.toList());
    }

    public Optional<ClientServer> getProxyServer(String nameIn) {
        ClientInfo zulfServerInfo = proxyServers.get(nameIn);
        return zulfServerInfo != null ? Optional.of(new ClientServer(nameIn, zulfServerInfo)) : Optional.empty();
    }

    public void setThisServer(ClientServer thisServer) {
        this.thisServer = thisServer;
    }

    // returns the first assigned connection as of now.
    public Optional<ClientServer> getAsServer() {
        return Optional.ofNullable(thisServer);
    }

    public boolean proxyServerOnline(String nameIn) {
        return proxyServers.containsKey(nameIn);
    }

    public void addNamedConnection(String nameIn, Connection connectionIn) {

        if (!(addressNames.containsValue(nameIn) || connectionNames.containsKey(nameIn))) {
            addressNames.put(connectionIn.getAddress(), nameIn);
            connectionNames.put(nameIn, connectionIn);
        }

    }

    public void removeConnection(Connection connectionIn) {
        allConnections.remove(connectionIn);
        String name = addressNames.remove(connectionIn.getAddress());
        if (name != null) {
            connectionNames.remove(name);
        }
    }

    // TODO: Re-do all of this at some point to buffer scripts instead of sending them in one large packet.
    public void processGlobalScript(@NotNull ScriptInfo infoIn) {

        ScriptAction action = infoIn.getScriptAction();

        CommandSender sender = pluginInstance.getServer().getConsoleSender();

        if (infoIn.getSender() != null) {

            ClientPlayer playerIn = infoIn.getSender();
            Player playerOut = pluginInstance.getServer().getPlayer(playerIn.getUuid());

            if (playerOut != null) {
                sender = playerOut;
            }

        }

        getPluginInstance().getTaskManager().submitSupplier(new GlobalScriptsTask(pluginInstance, infoIn.getScriptName(), action, sender, infoIn.getScriptData()))
                .thenAccept(file -> {
                    switch (action) {
                        case RELOAD:
                            if (!scriptFiles.contains(file)) {
                                scriptFiles.add(file);
                            }
                            break;
                        case DELETE:
                            scriptFiles.remove(file);
                            break;
                    }
                });

    }

    public void blockConnection(Connection connectionIn) {
        blockedConnections.add(connectionIn.getAddress());
        connectionIn.destroy();
    }

    public void newChannelConnection(SocketAddress socketAddressIn) {
        ChannelConnection channelConnection = new ChannelConnection(this, socketAddressIn);
        finaliseConnection(channelConnection);
    }

    public void newSocketConnection(Socket socketIn) throws IOException {
        SocketConnection socketConnection = new SocketConnection(this, socketIn);
        finaliseConnection(socketConnection);
    }

    private void finaliseConnection(Connection connectionIn) {
        allConnections.add(connectionIn);
        taskManager.newAsyncTask(connectionIn);
    }

    public boolean isBlocked(SocketAddress addressIn) {
        return blockedConnections.contains(addressIn);
    }

    public void register() {
        registered.incrementAndGet();
    }

    public void deRegister() {
        registered.decrementAndGet();
    }

    public int getRegistered() {
        return registered.get();
    }

    public void shutdown() {

        if (running.compareAndSet(true, false)) {

            for (Connection connection : allConnections) {
                connection.destroy();
            }

            connectionBarrier.release();

            for (File scriptFile : scriptFiles) {
                boolean delete = scriptFile.delete();
                if (!delete) {
                    pluginInstance.warning(String.format("Script %s could not be deleted. Does it exist?", scriptFile.getName()));
                }
            }

        }

    }

    public AtomicBoolean isRunning() {
        return running;
    }

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
    }

}
