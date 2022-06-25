package tk.zulfengaming.zulfbungee.spigot.socket;

import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.handlers.ClientListenerManager;
import tk.zulfengaming.zulfbungee.spigot.handlers.DataInHandler;
import tk.zulfengaming.zulfbungee.spigot.handlers.DataOutHandler;
import tk.zulfengaming.zulfbungee.spigot.handlers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.spigot.tasks.GlobalScriptsTask;
import tk.zulfengaming.zulfbungee.spigot.tasks.HeartbeatTask;
import tk.zulfengaming.zulfbungee.universal.socket.*;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientConnection implements Runnable {

    private final ZulfBungeeSpigot pluginInstance;

    // threads

    private final BukkitTask heartbeatThread;

    private final BukkitTask socketDaemon;
    private final BukkitTask dataInThread;
    private final BukkitTask dataOutThread;

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
    private final int heartbeatTicks;
    private final int packetResponseTime;

    private final List<File> scriptFiles = Collections.synchronizedList(new ArrayList<>());

    public ClientConnection(ZulfBungeeSpigot pluginInstanceIn) throws UnknownHostException {

        this.pluginInstance = pluginInstanceIn;
        this.clientListenerManager = new ClientListenerManager(this);

        this.packetHandlerManager = new PacketHandlerManager(this);
        this.heartbeatTicks = pluginInstanceIn.getYamlConfig().getInt("heartbeat-ticks");
        this.packetResponseTime = pluginInstanceIn.getYamlConfig().getInt("packet-response-time");

        socketBarrier = clientListenerManager.getSocketBarrier();

        HeartbeatTask heartbeatTask = new HeartbeatTask(this);
        this.heartbeatThread = pluginInstance.getTaskManager().newRepeatingTask(heartbeatTask, "Heartbeat", heartbeatTicks);


        this.dataInHandler = new DataInHandler(clientListenerManager, this);
        this.dataOutHandler = new DataOutHandler(clientListenerManager, this);

        socketBarrier.register();
        socketDaemon = pluginInstance.getTaskManager().newTask(clientListenerManager, "ClientListenerManager");

        this.dataInThread = pluginInstance.getTaskManager().newTask(dataInHandler, "DataIn");
        this.dataOutThread = pluginInstance.getTaskManager().newTask(dataOutHandler, "DataOut");


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

                    socket = clientListenerManager.getSocketHandoff().take();
                }

            } catch (InterruptedException ignored) {

            }

        } while (running.get());

    }

    public Optional<Packet> read() throws InterruptedException {
        return clientListenerManager.isSocketConnected().get() ? Optional.ofNullable(skriptPacketQueue.poll(packetResponseTime, TimeUnit.MILLISECONDS)) : Optional.empty();
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
            return read();
        } catch (InterruptedException e) {
            pluginInstance.warning(String.format("Packet: %s", packetIn.toString()));
            pluginInstance.warning("was interrupted being sent.");
        }

        return Optional.empty();
    }

    public void processGlobalScript(@NotNull ScriptInfo infoIn) {

        ScriptAction action = infoIn.getScriptAction();

        CompletableFuture.supplyAsync(new GlobalScriptsTask(this, infoIn.getScriptName(), action, infoIn.getScriptData()))
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

    public ServerInfo getClientInfo() {
        return clientListenerManager.getClientInfo();
    }

    public int getHeartbeatTicks() {
        return heartbeatTicks;
    }

    public void shutdown() throws IOException {

        for (File scriptFile : scriptFiles) {

            boolean deleted = scriptFile.delete();

            if (deleted) {
                pluginInstance.logDebug("Deleted script file " + scriptFile.getName() + " successfully.");
            } else {
                pluginInstance.warning("Failed to delete script file " + scriptFile.getName() + ". Does it exist?");
            }

        }

        if (running.compareAndSet(true, false)) {

            dataInThread.cancel();
            dataOutThread.cancel();

            clientListenerManager.shutdown();

            heartbeatThread.cancel();

            socketDaemon.cancel();

        }

    }

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
    }

    public void setConnectionName(String connectionNameIn) {
        this.connectionName = connectionNameIn;

    }

    public Optional<String> getConnectionName() {
        return Optional.ofNullable(connectionName);
    }

}
