package tk.zulfengaming.bungeesk.spigot.socket;

import org.bukkit.scheduler.BukkitTask;
import tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot;
import tk.zulfengaming.bungeesk.spigot.interfaces.ClientEvents;
import tk.zulfengaming.bungeesk.spigot.interfaces.ClientListener;
import tk.zulfengaming.bungeesk.spigot.interfaces.ClientManager;
import tk.zulfengaming.bungeesk.spigot.task.HeartbeatTask;
import tk.zulfengaming.bungeesk.universal.socket.Packet;

import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Optional;
import java.util.concurrent.*;

public class ClientConnection implements Runnable, ClientEvents {

    private final BungeeSkSpigot pluginInstance;

    private final BukkitTask heartbeatThread;

    private Socket socket;

    // the lastest packet from the queue coming in.
    private Packet packetBuffer;

    private boolean running = true;

    private boolean connected = true;

    private final PacketHandlerManager packetHandlerManager;

    private final ClientManager clientManager;


    public ClientConnection(BungeeSkSpigot pluginInstanceIn) throws UnknownHostException {

        this.pluginInstance = pluginInstanceIn;

        this.packetHandlerManager = new PacketHandlerManager(this);

        this.clientManager = new ClientManager(pluginInstanceIn);

        HeartbeatTask heartbeatTask = new HeartbeatTask(clientManager);

        this.heartbeatThread = pluginInstanceIn.getTaskManager().newRepeatingTask(heartbeatTask, "Heartbeat", pluginInstanceIn.getYamlConfig().getInt("heartbeat-ticks"));

        init();

    }

    private void init() {

    }


    public void run() {

        // TODO: Wait for SyncQueue here! while other thread does object listening

        do {
            try {

                if (clientManager.isSocketConnected()) {
                    Packet packetIn = clientManager.getQueueIn().take();

                    packetHandlerManager.handlePacket(packetIn, socket.getRemoteSocketAddress());
                    packetBuffer = packetIn;

                } else {
                    Optional<Socket> optionalSocket = clientManager.getSocket();

                    optionalSocket.ifPresent(value -> socket = value);
                }

            } catch (InterruptedException | ExecutionException | TimeoutException e) {

                e.printStackTrace();
            }

        } while (running);

    }


    @Override
    public void onDisconnect() {
        packetBuffer = null;

    }

    @Override
    public void onShutdown() {
        pl.log("Shutting down...");

        heartbeatThread.cancel();

    }

    public Optional<Packet> read() {
        return Optional.ofNullable(packetBuffer);
    }

    public void send_direct(Packet packetIn) {

        try {
            queueOut.put(packetIn);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public Optional<Packet> send(Packet packetIn) {

        send_direct(packetIn);

        return read();

    }

    public boolean isRunning() {
        return running;
    }

    public boolean isConnected() {
        return connected;
    }

}
