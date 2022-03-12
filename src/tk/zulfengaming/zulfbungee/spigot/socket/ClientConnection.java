package tk.zulfengaming.zulfbungee.spigot.socket;

import ch.njol.skript.Skript;
import org.bukkit.scheduler.BukkitTask;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.handlers.ClientListenerManager;
import tk.zulfengaming.zulfbungee.spigot.handlers.DataInHandler;
import tk.zulfengaming.zulfbungee.spigot.handlers.DataOutHandler;
import tk.zulfengaming.zulfbungee.spigot.handlers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.spigot.task.tasks.GlobalScriptsTask;
import tk.zulfengaming.zulfbungee.spigot.task.tasks.HeartbeatTask;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.ClientUpdate;
import tk.zulfengaming.zulfbungee.universal.socket.ServerInfo;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TransferQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientConnection implements Runnable {

    private final ZulfBungeeSpigot pluginInstance;

    // threads

    private final BukkitTask heartbeatThread;

    private final BukkitTask socketDaemon;

    private BukkitTask globalScriptsThread;

    private Socket socket;

    // the latest packet from the queue coming in.
    private final TransferQueue<Packet> skriptPacketQueue = new LinkedTransferQueue<>();

    private final AtomicBoolean running = new AtomicBoolean(true);

    // managers

    private final PacketHandlerManager packetHandlerManager;

    private final ClientListenerManager clientListenerManager;

    private final GlobalScriptsTask globalScriptManager;

    // other tasks

    private final Phaser socketBarrier;

    private final DataOutHandler dataOutHandler;

    private final DataInHandler dataInHandler;

    private final int heartbeatTicks;

    // misc. info

    private ClientUpdate clientUpdate;

    public ClientConnection(ZulfBungeeSpigot pluginInstanceIn) throws UnknownHostException {

        this.pluginInstance = pluginInstanceIn;

        this.clientListenerManager = new ClientListenerManager(this);

        this.packetHandlerManager = new PacketHandlerManager(this);

        this.heartbeatTicks = pluginInstance.getYamlConfig().getInt("heartbeat-ticks");

        socketBarrier = clientListenerManager.getSocketBarrier();

        HeartbeatTask heartbeatTask = new HeartbeatTask(this);

        this.heartbeatThread = pluginInstance.getTaskManager().newRepeatingTask(heartbeatTask, "Heartbeat", heartbeatTicks);

        this.globalScriptManager = new GlobalScriptsTask(this);

        this.dataInHandler = new DataInHandler(clientListenerManager, this);
        this.dataOutHandler = new DataOutHandler(clientListenerManager, this);

        socketBarrier.register();

        socketDaemon = pluginInstance.getTaskManager().newTask(clientListenerManager, "ClientListenerManager");

        pluginInstance.getTaskManager().newTask(dataInHandler, "DataIn");
        pluginInstance.getTaskManager().newTask(dataOutHandler, "DataOut");


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

        socketBarrier.arriveAndDeregister();

    }

    public Optional<Packet> read() throws InterruptedException {

        if (clientListenerManager.isSocketConnected().get()) {

            return Optional.ofNullable(skriptPacketQueue.poll(1, TimeUnit.SECONDS));

        } else {

            return Optional.empty();

        }
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
            e.printStackTrace();
        }

    }

    public Optional<Packet> send(Packet packetIn) throws InterruptedException {

        send_direct(packetIn);

        return read();

    }

    public void requestGlobalScripts() {
        globalScriptsThread = pluginInstance.getTaskManager().newTask(globalScriptManager, "GlobalScriptsTask");
    }

    public int getHeartbeatTicks() {
        return heartbeatTicks;
    }

    public GlobalScriptsTask getGlobalScriptManager() {
        return globalScriptManager;
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

    public void shutdown() throws IOException {

        if (running.compareAndSet(true, false)) {

            heartbeatThread.cancel();

            if (globalScriptsThread != null) {
                globalScriptsThread.cancel();
            }

            socketDaemon.cancel();

            clientListenerManager.shutdown();

        }

        if (clientUpdate != null) {

            for (String scriptName : clientUpdate.getScriptNames()) {

                File scriptFile = new File(Skript.getInstance().getDataFolder() + File.separator + "scripts",
                        scriptName);

                boolean deleted = scriptFile.delete();

                if (deleted) {
                    pluginInstance.logDebug("Deleted script file " + scriptName + " successfully.");
                } else {
                    pluginInstance.warning("Failed to delete script file " + scriptName + ". Does it exist?");
                }

            }

        }
    }

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
    }


    public void setClientUpdate(ClientUpdate clientUpdate) {
        this.clientUpdate = clientUpdate;
    }

    public Optional<ClientUpdate> getClientUpdate() {
        return Optional.ofNullable(clientUpdate);
    }
}
