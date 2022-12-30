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
import tk.zulfengaming.zulfbungee.spigot.handlers.TaskManager;
import tk.zulfengaming.zulfbungee.spigot.managers.ClientListenerManager;
import tk.zulfengaming.zulfbungee.spigot.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.spigot.tasks.GlobalScriptsTask;
import tk.zulfengaming.zulfbungee.spigot.tasks.HeartbeatTask;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.*;

import java.io.File;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientConnection extends BukkitRunnable {

    private final ZulfBungeeSpigot pluginInstance;

    // threads

    private final BukkitTask heartbeatThread;

    private Socket socket;

    // the latest packet from the queue coming in.
    private final TransferQueue<Packet> skriptPacketQueue = new LinkedTransferQueue<>();

    private final AtomicBoolean running = new AtomicBoolean(true);

    // managers

    private final PacketHandlerManager packetHandlerManager;

    private final ClientListenerManager clientListenerManager;

    private final List<File> scriptFiles = Collections.synchronizedList(new ArrayList<>());

    private final HashMap<String, ServerInfo> proxyServers = new HashMap<>();

    // other tasks

    private final Phaser socketBarrier;

    private final DataOutHandler dataOutHandler;

    private final DataInHandler dataInHandler;

    // misc. info

    private String connectionName = "";
    private int timeout = 2000;
    private int packetResponseTime = 1000;

    public ClientConnection(ZulfBungeeSpigot pluginInstanceIn, int timeoutIn, int heartbeatIn, int packetResponseTimeIn) throws UnknownHostException {

        this.pluginInstance = pluginInstanceIn;
        this.clientListenerManager = new ClientListenerManager(this);

        this.packetHandlerManager = new PacketHandlerManager(this);

        if (timeoutIn != 0) {
            timeout = timeoutIn;
        }
        if (packetResponseTimeIn != 0) {
            packetResponseTime = packetResponseTimeIn;
        }

        TaskManager taskManager = pluginInstance.getTaskManager();

        HeartbeatTask heartbeatTask = new HeartbeatTask(this);
        this.heartbeatThread = taskManager.newAsyncTickTask(heartbeatTask, heartbeatIn);

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

                    Packet packetIn = dataInHandler.getDataQueue().poll(1, TimeUnit.SECONDS);

                    if (packetIn != null) {

                        if (packetIn.shouldHandle()) {

                            packetHandlerManager.handlePacket(packetIn, socket.getRemoteSocketAddress());

                        } else {
                            skriptPacketQueue.tryTransfer(packetIn);
                        }

                    }

                } else {

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

                dataOutHandler.getDataQueue().put(packetIn);

                if (packetIn.getType() != PacketTypes.HEARTBEAT) {
                    pluginInstance.logDebug("Sent packet " + packetIn.getType().toString() + "...");
                }
            }

        } catch (InterruptedException e) {
            pluginInstance.error("That packet failed to send due to thread interruption?:");
            pluginInstance.error(packetIn.toString());
        }

    }

    public Optional<Packet> send(Packet packetIn) {

        sendDirect(packetIn);

        try {

            Packet poll = skriptPacketQueue.poll(packetResponseTime, TimeUnit.MILLISECONDS);

            if (poll == null) {
                pluginInstance.logDebug(ChatColor.YELLOW + packetIn.toString());
                pluginInstance.logDebug(ChatColor.YELLOW + "was dropped! This could have been caused by the server skipping ticks.");
                pluginInstance.logDebug(ChatColor.YELLOW + "Please try adjusting your packet response time in the config.");
            }

            return Optional.ofNullable(poll);

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

            ProxyPlayer playerIn = infoIn.getSender();
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

    public void setProxyServers(ProxyServer[] serverList) {
        proxyServers.clear();
        Arrays.stream(serverList).forEach(server -> proxyServers.put(server.getName(), server.getServerInfo()));
    }

    public ProxyServer[] getProxyServers() {
        return proxyServers.keySet().stream().map(ProxyServer::new).toArray(ProxyServer[]::new);
    }

    public Optional<ProxyServer> getProxyServer(String nameIn) {
        ServerInfo serverInfo = proxyServers.get(nameIn);
        return serverInfo != null ? Optional.of(new ProxyServer(nameIn, serverInfo)) : Optional.empty();
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

    public ServerInfo getClientInfo() {
        return clientListenerManager.getClientInfo();
    }

    public int getTimeout() {
        return timeout;
    }

    public void shutdown() {

        if (running.compareAndSet(true, false)) {

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

    public void setName(String connectionNameIn) {
        this.connectionName = connectionNameIn;

    }

    public String getName() {
        return connectionName;
    }

}
