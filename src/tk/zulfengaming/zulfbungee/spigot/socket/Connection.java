package tk.zulfengaming.zulfbungee.spigot.socket;

import org.bukkit.scheduler.BukkitRunnable;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.managers.ConnectionManager;
import tk.zulfengaming.zulfbungee.spigot.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientInfo;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Connection extends BukkitRunnable {

    private final ConnectionManager connectionManager;

    protected final AtomicBoolean running = new AtomicBoolean(true);

    protected final LinkedBlockingQueue<Optional<Packet>> skriptPacketQueue = new LinkedBlockingQueue<>();

    protected final ZulfBungeeSpigot pluginInstance;

    protected final PacketHandlerManager packetHandlerManager;

    protected final ClientInfo clientInfo;

    public Connection(ConnectionManager connectionManagerIn) throws IOException {
        this.connectionManager = connectionManagerIn;
        this.pluginInstance = connectionManagerIn.getPluginInstance();
        this.packetHandlerManager = new PacketHandlerManager(this);
        this.clientInfo = new ClientInfo(pluginInstance.getServer().getMaxPlayers(), pluginInstance.getServer().getPort());
        connectionManager.register();
    }

    public abstract void sendDirect(Packet packetIn);

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
    }

    public LinkedBlockingQueue<Optional<Packet>> getSkriptPacketQueue() {
        return skriptPacketQueue;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public abstract SocketAddress getAddress();

    public abstract void end();

    public synchronized void shutdown() {
        connectionManager.deRegister();
        connectionManager.removeConnection(this);
        end();
    }

    @Override
    public abstract void run();

}
