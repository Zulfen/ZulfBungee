package tk.zulfengaming.zulfbungee.spigot.socket;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.handlers.DataInHandler;
import tk.zulfengaming.zulfbungee.spigot.handlers.DataOutHandler;
import tk.zulfengaming.zulfbungee.spigot.managers.ClientListenerManager;
import tk.zulfengaming.zulfbungee.spigot.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.spigot.managers.TaskManager;
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
import java.net.Socket;
import java.util.*;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientConnection extends BukkitRunnable {

    private final ZulfBungeeSpigot pluginInstance;

    // threads

    private final BukkitTask heartbeatThread;

    private Socket socket;

    // the latest packet from the queue coming in.
    private final LinkedTransferQueue<Optional<Packet>> skriptPacketQueue = new LinkedTransferQueue<>();

    private final AtomicBoolean running = new AtomicBoolean(true);

    // managers

    private final PacketHandlerManager packetHandlerManager;

    private final ClientListenerManager clientListenerManager;

    private final List<File> scriptFiles = Collections.synchronizedList(new ArrayList<>());

    private final HashMap<String, ClientInfo> proxyServers = new HashMap<>();

    // other tasks

    private final Phaser socketBarrier;

    private final DataOutHandler dataOutHandler;

    private final DataInHandler dataInHandler;

    private final HeartbeatTask heartbeatTask;

    // misc. info

    private String connectionName = "";
    private int timeout = 2000;

    public ClientConnection(ZulfBungeeSpigot pluginInstanceIn) {

        this.pluginInstance = pluginInstanceIn;
        this.clientListenerManager = new ClientListenerManager(this);

        this.packetHandlerManager = new PacketHandlerManager(this);

        TaskManager taskManager = pluginInstance.getTaskManager();

        this.heartbeatTask = new HeartbeatTask(this);
        this.heartbeatThread = taskManager.newAsyncTickTask(heartbeatTask, 20);

        this.dataInHandler = new DataInHandler(this);
        this.dataOutHandler = new DataOutHandler(this);

        socketBarrier = clientListenerManager.getSocketBarrier();
        socketBarrier.register();

        taskManager.newAsyncTask(clientListenerManager);
        taskManager.newAsyncTask(dataInHandler);
        taskManager.newAsyncTask(dataOutHandler);

    }


    public void run() {

        Thread.currentThread().setName("ClientConnection");

        do {
            try {

                if (clientListenerManager.isSocketConnected().get()) {

                    Optional<Packet> packetIn = dataInHandler.getDataQueue().take();

                    if (packetIn.isPresent()) {

                        Packet packet = packetIn.get();

                        if (packet.shouldHandle()) {
                            packetHandlerManager.handlePacket(packet, socket.getRemoteSocketAddress());
                        } else {
                            skriptPacketQueue.put(packetIn);
                        }

                    } else {

                        while (skriptPacketQueue.hasWaitingConsumer()) {
                            skriptPacketQueue.tryTransfer(Optional.empty());
                        }

                    }


                } else {

                    dataOutHandler.getDataQueue().offer(Optional.empty());
                    skriptPacketQueue.offer(Optional.empty());

                    pluginInstance.logDebug(String.format("Thread has arrived: %s", Thread.currentThread().getName()));

                    socketBarrier.arriveAndAwaitAdvance();

                    Optional<Socket> socketOptional = clientListenerManager.getSocketHandoff().take();

                    if (clientListenerManager.isTerminated().get()) {
                        break;
                    } else socketOptional.ifPresent(value -> socket = value);

                }

            } catch (InterruptedException e) {
                break;
            }

        } while (running.get());

        socketBarrier.arriveAndDeregister();

    }


    public void sendDirect(Packet packetIn) {

        try {

            if (clientListenerManager.isSocketConnected().get()) {

                dataOutHandler.getDataQueue().put(Optional.of(packetIn));

                if (packetIn.getType() != PacketTypes.HEARTBEAT) {
                    pluginInstance.logDebug("Sent packet " + packetIn + "...");
                }
            }

        } catch (InterruptedException e) {
            pluginInstance.error("That packet failed to send due to thread interruption?:");
            pluginInstance.error(packetIn.toString());
        }

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

    public Optional<Packet> send(Packet packetIn) {

        sendDirect(packetIn);

        try {

            if (clientListenerManager.isSocketConnected().get()) {

                Optional<Packet> poll = skriptPacketQueue.take();

                if (!poll.isPresent()) {
                    pluginInstance.logDebug(ChatColor.YELLOW + packetIn.toString());
                    pluginInstance.logDebug(ChatColor.YELLOW + "was dropped! This could have been caused by the server skipping ticks.");
                    pluginInstance.logDebug(ChatColor.YELLOW + "Please try adjusting your packet response time in the config.");
                }

                return poll;

            }

        } catch (InterruptedException e) {
            pluginInstance.warning(String.format("Packet: %s", packetIn.toString()));
            pluginInstance.warning("was interrupted being read.");
        }

        return Optional.empty();
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

    public void setProxyServers(ClientServer[] serverList) {
        proxyServers.clear();
        Arrays.stream(serverList).forEach(server -> proxyServers.put(server.getName(), server.getClientInfo()));
    }

    public ClientServer[] getProxyServers() {
        return proxyServers.entrySet().stream()
                .map(server -> new ClientServer(server.getKey(), server.getValue()))
                .toArray(ClientServer[]::new);
    }

    public Optional<ClientServer> getProxyServer(String nameIn) {
        ClientInfo zulfServerInfo = proxyServers.get(nameIn);
        return zulfServerInfo != null ? Optional.of(new ClientServer(nameIn, zulfServerInfo)) : Optional.empty();
    }

    public Optional<ClientServer> getAsServer() {

        ClientInfo clientInfo = proxyServers.get(connectionName);

        if (clientInfo != null) {
            return Optional.of(new ClientServer(connectionName, clientInfo));
        }

        return Optional.empty();

    }

    public boolean proxyServerOnline(String nameIn) {
        return proxyServers.containsKey(nameIn);
    }

    public AtomicBoolean isRunning() {
        return running;
    }

    public AtomicBoolean isConnected() {
        return clientListenerManager.isSocketConnected();
    }

    public ClientInfo getClientInfo() {
        return clientListenerManager.getClientInfo();
    }

    public int getTimeout() {
        return timeout;
    }

    public void shutdown() {

        if (running.compareAndSet(true, false) && clientListenerManager.isTerminated().compareAndSet(false, true)) {

            heartbeatThread.cancel();
            clientListenerManager.shutdown();

            for (File scriptFile : scriptFiles) {

                boolean deleted = scriptFile.delete();

                if (deleted) {
                    pluginInstance.logDebug("Deleted script file " + scriptFile.getName() + " successfully.");
                } else {
                    pluginInstance.warning("Failed to delete script file " + scriptFile.getName() + ". Does it exist?");
                }

            }

        }

    }

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
    }

    public ClientListenerManager getClientListenerManager() {
        return clientListenerManager;
    }

    public HeartbeatTask getHeartbeatTask() {
        return heartbeatTask;
    }

    public void setName(String connectionNameIn) {
        this.connectionName = connectionNameIn;
    }

    public String getName() {
        return connectionName;
    }

}
