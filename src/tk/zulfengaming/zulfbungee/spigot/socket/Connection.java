package tk.zulfengaming.zulfbungee.spigot.socket;

import org.bukkit.scheduler.BukkitRunnable;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.interfaces.transport.ClientCommHandler;
import tk.zulfengaming.zulfbungee.spigot.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientInfo;

import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Connection extends BukkitRunnable {

    protected final ZulfBungeeSpigot pluginInstance;
    protected final PacketHandlerManager packetHandlerManager;
    protected ClientCommHandler clientCommHandler;

    protected final ClientInfo clientInfo;
    protected final SocketAddress socketAddress;

    protected final AtomicBoolean connected = new AtomicBoolean(true);

    private final String forcedName;
    private final LinkedTransferQueue<Optional<Packet>> skriptQueue = new LinkedTransferQueue<>();

    public Connection(ZulfBungeeSpigot pluginInstanceIn, SocketAddress socketAddressIn) {

        this.pluginInstance = pluginInstanceIn;
        this.socketAddress = socketAddressIn;
        this.packetHandlerManager = new PacketHandlerManager(this);

        this.forcedName = pluginInstance.getConfig().getString("forced-connection-name");


        this.clientInfo = new ClientInfo(pluginInstance.getServer().getMaxPlayers(), pluginInstance.getServer().getPort());

    }


    @Override
    public void run() {

        clientCommHandler.start();

        if (forcedName.isEmpty()) {
            sendDirect(new Packet(PacketTypes.PROXY_CLIENT_INFO, true, true, clientInfo));
        } else {
            sendDirect(new Packet(PacketTypes.CONNECTION_NAME, true, true, new Object[]{forcedName, clientInfo}));
        }
        sendDirect(new Packet(PacketTypes.GLOBAL_SCRIPT, true, true, new Object[0]));

        while (connected.get()) {

            Optional<Packet> read = clientCommHandler.readPacket();

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
            pluginInstance.logDebug("Sent packet " + packetIn.getType() + "...");
        }
    }

    public Optional<Packet> read() {
        try {
            return skriptQueue.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Optional.empty();
    }

    public void destroy() {
        if (connected.compareAndSet(true, false)) {
            clientCommHandler.destroy();
        }
    }

    protected void setClientCommHandler(ClientCommHandler handlerIn) {
        this.clientCommHandler = handlerIn;
        clientCommHandler.setConnection(this);
    }

    public SocketAddress getAddress() {
        return socketAddress;
    }

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
    }

}
