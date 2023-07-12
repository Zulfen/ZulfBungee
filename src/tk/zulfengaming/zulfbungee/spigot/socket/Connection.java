package tk.zulfengaming.zulfbungee.spigot.socket;

import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.interfaces.ClientCommHandler;
import tk.zulfengaming.zulfbungee.spigot.managers.ConnectionManager;
import tk.zulfengaming.zulfbungee.spigot.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientInfo;

import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Connection extends BukkitRunnable {

    protected final ConnectionManager connectionManager;

    protected final ZulfBungeeSpigot pluginInstance;
    protected final PacketHandlerManager packetHandlerManager;
    protected final ClientCommHandler clientCommHandler;

    protected final ClientInfo clientInfo;
    protected final SocketAddress socketAddress;

    protected final AtomicBoolean connected = new AtomicBoolean(true);

    private final LinkedTransferQueue<Optional<Packet>> skriptQueue = new LinkedTransferQueue<>();

    private final String forcedName;

    public Connection(ConnectionManager connectionManagerIn, ClientCommHandler commHandlerIn, SocketAddress socketAddressIn) {

        this.connectionManager = connectionManagerIn;
        this.pluginInstance = connectionManager.getPluginInstance();
        this.socketAddress = socketAddressIn;
        this.packetHandlerManager = new PacketHandlerManager(this);

        this.forcedName = pluginInstance.getConfig().getString("forced-connection-name");
        this.clientCommHandler = commHandlerIn;
        clientCommHandler.setConnection(this);

        this.clientInfo = new ClientInfo(pluginInstance.getServer().getMaxPlayers(), pluginInstance.getServer().getPort());
        connectionManager.register();

    }


    @Override
    public void run() {

        clientCommHandler.start();
        pluginInstance.logInfo(String.format("%sConnection established with proxy! (%s)", ChatColor.GREEN, socketAddress));

        if (forcedName.isEmpty()) {
            sendDirect(new Packet(PacketTypes.PROXY_CLIENT_INFO, true, true, clientInfo));
        } else {
            sendDirect(new Packet(PacketTypes.CONNECTION_NAME, true, true, new Object[]{forcedName, clientInfo}));
        }

        sendDirect(new Packet(PacketTypes.GLOBAL_SCRIPT, true, true, new Object[0]));

        while (connected.get()) {

            Optional<Packet> read = clientCommHandler.read();

            if (read.isPresent()) {

                Packet packet = read.get();

                if (packet.shouldHandle()) {
                    packetHandlerManager.handlePacket(packet);
                } else {
                    skriptQueue.put(read);
                }

            } else {
                skriptQueue.put(Optional.empty());
            }

        }
        
    }

    public void sendDirect(Packet packetIn) {
        clientCommHandler.send(packetIn);
        if (packetIn.getType() != PacketTypes.HEARTBEAT_PROXY) {
            pluginInstance.logDebug("Sent packet " + packetIn + "...");
        }
    }

    public Optional<Packet> read() throws InterruptedException {
        return skriptQueue.take();
    }

    public void destroy() {
        if (connected.compareAndSet(true, false)) {
            clientCommHandler.destroy();
            connectionManager.deRegister();
            connectionManager.removeConnection(this);
        }
    }

    public SocketAddress getAddress() {
        return socketAddress;
    }

    public String getForcedName() {
        return forcedName;
    }

    public ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
    }

    public AtomicBoolean isConnected() {
        return connected;
    }

}
