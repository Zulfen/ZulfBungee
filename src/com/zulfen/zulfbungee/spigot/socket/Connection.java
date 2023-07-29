package com.zulfen.zulfbungee.spigot.socket;

import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.interfaces.transport.ClientCommHandler;
import com.zulfen.zulfbungee.spigot.managers.ConnectionManager;
import com.zulfen.zulfbungee.spigot.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.HandshakePacket;
import org.bukkit.scheduler.BukkitRunnable;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientInfo;

import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Connection<T> extends BukkitRunnable {

    protected final ConnectionManager<T> connectionManager;
    protected final ZulfBungeeSpigot pluginInstance;
    protected final PacketHandlerManager packetHandlerManager;
    protected ClientCommHandler<T> clientCommHandler;

    protected final ClientInfo clientInfo;
    protected final SocketAddress socketAddress;

    protected final AtomicBoolean connected = new AtomicBoolean(false);

    private final String forcedName;
    private final LinkedTransferQueue<Optional<Packet>> skriptQueue = new LinkedTransferQueue<>();

    public Connection(ConnectionManager<T> connectionManager, SocketAddress socketAddressIn) {

        this.connectionManager = connectionManager;
        this.pluginInstance = connectionManager.getPluginInstance();
        this.socketAddress = socketAddressIn;
        this.packetHandlerManager = new PacketHandlerManager(this);

        this.forcedName = pluginInstance.getConfig().getString("forced-connection-name");

        this.clientInfo = new ClientInfo(pluginInstance.getServer().getMaxPlayers(), pluginInstance.getServer().getPort(),
                pluginInstance.getServer().getVersion());

    }

    public void start() {
        pluginInstance.getTaskManager().newAsyncTask(this);
    }

    public abstract void onRegister();

    @Override
    public void run() {

        if (forcedName.isEmpty()) {
            sendDirect(new HandshakePacket(PacketTypes.PROXY_CLIENT_INFO, clientInfo));
        } else {
            sendDirect(new HandshakePacket(PacketTypes.CONNECTION_NAME, new Object[]{forcedName, clientInfo}));
        }
        sendDirect(new HandshakePacket(PacketTypes.GLOBAL_SCRIPT, new Object[0]));

        clientCommHandler.awaitProperConnection();
        connected.set(true);
        connectionManager.register(this);
        onRegister();

        while (connected.get()) {

            Optional<Packet> read = clientCommHandler.readPacket();

            if (read.isPresent()) {

                Packet packet = read.get();

                if (packet.shouldHandle()) {
                    packetHandlerManager.handlePacket(packet);
                } else {
                    skriptQueue.offer(read);
                }

            } else {
                skriptQueue.offer(Optional.empty());
            }

        }
        
    }

    public void sendDirect(Packet packetIn) {
        clientCommHandler.writePacket(packetIn);
        if (packetIn.getType() != PacketTypes.HEARTBEAT_PROXY) {
            pluginInstance.logDebug("Sent packet " + packetIn.getType() + "...");
        }
    }

    public Optional<Packet> read() {
        if (connected.get()) {
            try {
                return skriptQueue.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        return Optional.empty();
    }

    public void destroy() {
        connected.set(false);
        clientCommHandler.destroy();
        connectionManager.deRegister(this);
    }

    protected void setClientCommHandler(ClientCommHandler<T> handlerIn) {
        this.clientCommHandler = handlerIn;
        clientCommHandler.setConnection(this);
    }

    public ClientCommHandler<T> getClientCommHandler() {
        return clientCommHandler;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    public SocketAddress getAddress() {
        return socketAddress;
    }

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
    }

}
