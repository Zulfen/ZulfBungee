package tk.zulfengaming.zulfbungee.spigot.socket;

import org.bukkit.scheduler.BukkitTask;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.handlers.ClientListenerManager;
import tk.zulfengaming.zulfbungee.spigot.handlers.DataInHandler;
import tk.zulfengaming.zulfbungee.spigot.handlers.DataOutHandler;
import tk.zulfengaming.zulfbungee.spigot.handlers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.spigot.task.tasks.HeartbeatTask;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Phaser;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClientConnection implements Runnable {

    private final ZulfBungeeSpigot pluginInstance;

    // threads

    private BukkitTask heartbeatThread;

    private Socket socket;

    // the latest packet from the queue coming in.
    private final BlockingQueue<Packet> skriptPacketQueue = new SynchronousQueue<>();

    private final AtomicBoolean running = new AtomicBoolean(true);

    // managers

    private final PacketHandlerManager packetHandlerManager;

    private final ClientListenerManager clientListenerManager;

    // other tasks

    private final Phaser socketBarrier;

    private DataOutHandler dataOutHandler;

    private DataInHandler dataInHandler;

    private BukkitTask socketDaemon;

    private final int heartbeatTicks;

    // identification

    private final String serverName;

    public ClientConnection(ZulfBungeeSpigot pluginInstanceIn) throws UnknownHostException {

        this.pluginInstance = pluginInstanceIn;

        this.clientListenerManager = new ClientListenerManager(this);

        this.packetHandlerManager = new PacketHandlerManager(this);

        this.serverName = pluginInstanceIn.getYamlConfig().getString("server-name");
        this.heartbeatTicks = pluginInstance.getYamlConfig().getInt("heartbeat-ticks");

        socketBarrier = clientListenerManager.getSocketBarrier();

        init();

    }

    private void init() {

        HeartbeatTask heartbeatTask = new HeartbeatTask(this);

        this.heartbeatThread = pluginInstance.getTaskManager().newRepeatingTask(heartbeatTask, "Heartbeat", heartbeatTicks);

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
                            skriptPacketQueue.put(packetIn);
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

            return Optional.ofNullable(skriptPacketQueue.poll(5, TimeUnit.SECONDS));

        } else {

            return Optional.empty();

        }
    }

    public void send_direct(Packet packetIn) {

        try {

            if (clientListenerManager.isSocketConnected().get()) {
                dataOutHandler.getDataQueue().put(packetIn);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (packetIn.getType() != PacketTypes.HEARTBEAT) {
            pluginInstance.logDebug("Sent packet " + packetIn.getType().toString() + "...");
        }

    }

    public Optional<Packet> send(Packet packetIn) throws InterruptedException {

        send_direct(packetIn);

        return read();

    }

    public int getHeartbeatTicks() {
        return heartbeatTicks;
    }

    public AtomicBoolean isRunning() {
        return running;
    }

    public boolean isConnected() {
        return clientListenerManager.isSocketConnected().get();
    }

    public ClientListenerManager getClientListenerManager() {
        return clientListenerManager;
    }

    public void shutdown() throws IOException {

        if (running.compareAndSet(true, false)) {

            heartbeatThread.cancel();
            socketDaemon.cancel();

            clientListenerManager.shutdown();

        }

    }

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
    }

    public String getServerName() {
        return serverName;
    }

}
