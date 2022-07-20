package tk.zulfengaming.zulfbungee.spigot.socket;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.handlers.DataInHandler;
import tk.zulfengaming.zulfbungee.spigot.handlers.DataOutHandler;
import tk.zulfengaming.zulfbungee.spigot.managers.ClientListenerManager;
import tk.zulfengaming.zulfbungee.spigot.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.spigot.tasks.GlobalScriptsTask;
import tk.zulfengaming.zulfbungee.spigot.tasks.HeartbeatTask;
import tk.zulfengaming.zulfbungee.universal.socket.*;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;

import java.io.File;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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

    // other tasks

    private final Phaser socketBarrier;

    private final DataOutHandler dataOutHandler;

    private final DataInHandler dataInHandler;
// misc. info

    private String connectionName;
    private final int timeout;
    private final int packetResponseTime;

    private final List<File> scriptFiles = Collections.synchronizedList(new ArrayList<>());

    public ClientConnection(ZulfBungeeSpigot pluginInstanceIn) throws UnknownHostException {

        this.pluginInstance = pluginInstanceIn;
        this.clientListenerManager = new ClientListenerManager(this);

        this.packetHandlerManager = new PacketHandlerManager(this);
        this.timeout = pluginInstance.getYamlConfig().getInt("connection-timeout");
        this.packetResponseTime = pluginInstance.getYamlConfig().getInt("packet-response-time");

        socketBarrier = clientListenerManager.getSocketBarrier();

        HeartbeatTask heartbeatTask = new HeartbeatTask(this);
        this.heartbeatThread = pluginInstance.getTaskManager().newAsyncTickTask(heartbeatTask, timeout);

        this.dataInHandler = new DataInHandler(this);
        this.dataOutHandler = new DataOutHandler(this);

        socketBarrier.register();

        pluginInstance.getTaskManager().newAsyncTask(clientListenerManager);
        pluginInstance.getTaskManager().newAsyncTask(dataInHandler);
        pluginInstance.getTaskManager().newAsyncTask(dataOutHandler);


    }


    public void run() {

        do {
            try {

                if (clientListenerManager.isSocketConnected().get()) {

                    Packet packetIn = dataInHandler.getDataQueue().poll(5, TimeUnit.SECONDS);

                    if (packetIn != null) {

                        if (packetIn.shouldHandle()) {

                            packetHandlerManager.handlePacket(packetIn, socket.getRemoteSocketAddress());

                        } else {
                            skriptPacketQueue.tryTransfer(packetIn);
                        }

                    }

                } else {

                    socketBarrier.arriveAndAwaitAdvance();

                    Optional<Socket> socketOptional = clientListenerManager.getSocketHandoff().take();

                    if (clientListenerManager.isTerminated().get()) {

                        socketBarrier.arriveAndDeregister();

                    } else socketOptional.ifPresent(value -> socket = value);

                }

            } catch (InterruptedException e) {
                socketBarrier.arriveAndDeregister();
            }

        } while (running.get());

    }


    public void send_direct(Packet packetIn) {

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

        send_direct(packetIn);

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
            sender = pluginInstance.getServer().getPlayer(playerIn.getUuid());
        }

        getPluginInstance().getTaskManager().submitSupplier(new GlobalScriptsTask(this, infoIn.getScriptName(), action, sender, infoIn.getScriptData()))
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

    public synchronized void shutdown() {

        if (running.compareAndSet(true, false)) {

            for (File scriptFile : scriptFiles) {

                boolean deleted = scriptFile.delete();

                if (deleted) {
                    pluginInstance.logDebug("Deleted script file " + scriptFile.getName() + " successfully.");
                } else {
                    pluginInstance.warning("Failed to delete script file " + scriptFile.getName() + ". Does it exist?");
                }

            }

            heartbeatThread.cancel();
            clientListenerManager.shutdown();

        }

    }

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
    }

    public ClientListenerManager getClientListenerManager() {
        return clientListenerManager;
    }

    public void setConnectionName(String connectionNameIn) {
        this.connectionName = connectionNameIn;

    }

    public Optional<String> getConnectionName() {
        return Optional.ofNullable(connectionName);
    }

}
