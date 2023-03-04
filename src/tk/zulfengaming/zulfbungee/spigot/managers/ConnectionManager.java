package tk.zulfengaming.zulfbungee.spigot.managers;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.spigot.tasks.ConnectionTask;
import tk.zulfengaming.zulfbungee.spigot.tasks.GlobalScriptsTask;
import tk.zulfengaming.zulfbungee.spigot.tasks.HeartbeatTask;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientInfo;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.NetworkVariable;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptAction;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptInfo;

import java.io.File;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ConnectionManager extends BukkitRunnable {

    private final ZulfBungeeSpigot pluginInstance;
    
    private final List<Connection> allConnections = Collections.synchronizedList(new ArrayList<>());

    private final Map<SocketAddress, String> addressNames = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, Connection> connectionNames = Collections.synchronizedMap(new HashMap<>());

    private final LinkedHashMap<String, ClientInfo> proxyServers = new LinkedHashMap<>();

    private final List<File> scriptFiles = Collections.synchronizedList(new ArrayList<>());

    private final AtomicBoolean running = new AtomicBoolean(true);

    private final AtomicInteger registered = new AtomicInteger();

    private final LinkedBlockingQueue<LinkedList<Packet>> connectionPackets = new LinkedBlockingQueue<>();

    private final CyclicBarrier connectionBarrier = new CyclicBarrier(2);
    private final BukkitTask connectionTask;

    private final BukkitTask heartbeatThread;
    private final HeartbeatTask heartbeatTask;

    public ConnectionManager(ZulfBungeeSpigot pluginIn, InetAddress clientAddress, int clientPort, InetAddress serverAddress, int serverPort, int timeOut) {
        this.pluginInstance = pluginIn;
        this.connectionTask = pluginInstance.getTaskManager().newAsyncTask(new ConnectionTask(this, connectionBarrier, clientAddress, clientPort, serverAddress, serverPort, timeOut));
        this.heartbeatTask = new HeartbeatTask(this);
        this.heartbeatThread = pluginInstance.getTaskManager().newAsyncTickTask(heartbeatTask, 20);
    }

    @Override
    public void run() {

        Thread.currentThread().setName("ConnectionManager");

        do {

            try {

                pluginInstance.warning("hi");
                connectionBarrier.await();
                pluginInstance.warning("bye");

                while (registered.get() > 0) {

                    LinkedList<Packet> packetsIn = new LinkedList<>();

                    for (Connection connection : allConnections) {

                        try {

                            Optional<Packet> getPacket = connection.getSkriptPacketQueue().take();

                            if (getPacket.isPresent()) {
                                packetsIn.add(getPacket.get());
                            } else {
                                break;
                            }

                        } catch (InterruptedException e) {
                            break;
                        }

                    }

                    try {
                        connectionPackets.put(packetsIn);
                    } catch (InterruptedException e) {
                        break;
                    }

                }

            } catch (BrokenBarrierException ignored) {
               // barrier only reset on shutdown, can be ignored
            } catch (InterruptedException e) {
                break;
            }

        } while(running.get());

    }

    public void sendDirect(Packet packetIn) {
        allConnections.forEach(connection -> connection.sendDirect(packetIn));
    }

    // returns the first packet it gets from any of the connections. keep this in mind.
    public Optional<Packet> send(Packet packetIn) {

        sendDirect(packetIn);

        try {

            LinkedList<Packet> list = connectionPackets.take();

            if (!list.isEmpty()) {
                return Optional.of(list.getFirst());
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return Optional.empty();

    }

    public List<ClientPlayer> getPlayers(ClientServer[] serversIn) {

        if (serversIn.length > 0) {
            sendDirect(new Packet(PacketTypes.PROXY_PLAYERS,
                    true, false, serversIn));
        } else {
            sendDirect(new Packet(PacketTypes.PROXY_PLAYERS,
                    true, false, new Object[0]));
        }


        try {

            LinkedList<Packet> list = connectionPackets.take();

            if (!list.isEmpty()) {

                return list.stream()
                        .map(Packet::getDataSingle)
                        .filter(Objects::nonNull)
                        .filter(ClientPlayer.class::isInstance)
                        .map(ClientPlayer.class::cast)
                        .collect(Collectors.toList());

            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return Collections.emptyList();

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

    public void addProxyServer(String nameIn, ClientInfo infoIn) {
        if (!proxyServers.containsKey(nameIn)) {
            proxyServers.put(nameIn, infoIn);
        }
    }

    public void removeProxyServer(String nameIn) {
        proxyServers.remove(nameIn);
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

    // returns the first assigned connection as of now.
    public Optional<ClientServer> getAsServer() {

        Optional<Map.Entry<String, ClientInfo>> getFirst = proxyServers.entrySet().stream().findFirst();

        if (getFirst.isPresent()) {
            Map.Entry<String, ClientInfo> server = getFirst.get();
            return Optional.of(new ClientServer(server.getKey(), server.getValue()));
        }

        return Optional.empty();

    }

    public boolean proxyServerOnline(String nameIn) {
        return proxyServers.containsKey(nameIn);
    }

    public void addInactiveConnection(Connection connectionIn) {
        allConnections.add(connectionIn);
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
                        case NEW:
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

            heartbeatThread.cancel();

            for (Connection connection : allConnections) {
                connection.shutdown();
                connection.cancel();
            }

            connectionBarrier.reset();
            connectionTask.cancel();

        }

    }
    public AtomicBoolean isRunning() {
        return running;
    }

    public HeartbeatTask getHeartbeatTask() {
        return heartbeatTask;
    }

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
    }

}
