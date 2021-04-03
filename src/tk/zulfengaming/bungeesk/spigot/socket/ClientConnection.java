package tk.zulfengaming.bungeesk.spigot.socket;

import org.bukkit.scheduler.BukkitTask;
import tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot;
import tk.zulfengaming.bungeesk.spigot.interfaces.ClientManager;
import tk.zulfengaming.bungeesk.spigot.task.HeartbeatTask;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.*;

public class ClientConnection implements Runnable {

    private final BungeeSkSpigot pluginInstance;

    private BukkitTask heartbeatThread;

    private Socket socket;

    // the lastest packet from the queue coming in.
    private BlockingQueue<Packet> skriptPacketQueue = new SynchronousQueue<>();

    private boolean running = true;

    private boolean connected = true;

    private final PacketHandlerManager packetHandlerManager;

    private final ClientManager clientManager;

    public ClientConnection(BungeeSkSpigot pluginInstanceIn) throws UnknownHostException {

        this.pluginInstance = pluginInstanceIn;

        this.packetHandlerManager = new PacketHandlerManager(this);

        this.clientManager = new ClientManager(pluginInstanceIn);

        init();

    }

    private void init() {

        HeartbeatTask heartbeatTask = new HeartbeatTask(clientManager);

        this.heartbeatThread = pluginInstance.getTaskManager().newRepeatingTask(heartbeatTask, "Heartbeat", pluginInstance.getYamlConfig().getInt("heartbeat-ticks"));


    }


    public void run() {

        // TODO: Wait for SyncQueue here! while other thread does object listening

        do {
            try {

                if (clientManager.isSocketConnected()) {
                    Packet packetIn = clientManager.getQueueIn().take();

                    if (packetIn.shouldHandle()) {
                        packetHandlerManager.handlePacket(packetIn, socket.getRemoteSocketAddress());

                    } else {
                        skriptPacketQueue.put(packetIn);
                    }

                } else {
                    Optional<Socket> optionalSocket = clientManager.getSocket();

                    optionalSocket.ifPresent(value -> socket = value);
                }

            } catch (InterruptedException | ExecutionException | TimeoutException e) {

                e.printStackTrace();
            }

        } while (running);

    }

    public Optional<Packet> read() throws InterruptedException {
        return Optional.ofNullable(skriptPacketQueue.poll(5, TimeUnit.SECONDS));
    }

    public void send_direct(Packet packetIn) {

        try {
            clientManager.getQueueOut().put(packetIn);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public Optional<Packet> send(Packet packetIn) throws InterruptedException {

        send_direct(packetIn);

        return read();

    }

    public boolean isRunning() {
        return running;
    }

    public boolean isConnected() {
        return connected;
    }

    public void shutdown() {
        heartbeatThread.cancel();
        clientManager.shutdown();

    }

    public BungeeSkSpigot getPluginInstance() {
        return pluginInstance;
    }

    public ClientManager getClientManager() {
        return clientManager;
    }
}
