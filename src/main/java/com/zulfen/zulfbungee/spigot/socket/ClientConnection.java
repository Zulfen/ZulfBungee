package com.zulfen.zulfbungee.spigot.socket;

import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.interfaces.ClientCommHandler;
import com.zulfen.zulfbungee.spigot.managers.ConnectionManager;
import com.zulfen.zulfbungee.spigot.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.interfaces.PacketConsumer;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientInfo;
import com.zulfen.zulfbungee.universal.socket.objects.client.HandshakePacket;
import com.zulfen.zulfbungee.universal.util.MultiplePacketQueue;
import com.zulfen.zulfbungee.universal.util.SinglePacketQueue;

import java.net.SocketAddress;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ClientConnection<T> implements PacketConsumer {

    protected final ConnectionManager<T> connectionManager;
    protected final ZulfBungeeSpigot pluginInstance;
    protected final PacketHandlerManager packetHandlerManager;
    protected ClientCommHandler<T> clientCommHandler;

    protected final ClientInfo clientInfo;

    protected final SocketAddress socketAddress;

    protected final ConcurrentHashMap<UUID, SinglePacketQueue> requests = new ConcurrentHashMap<>();

    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicBoolean properConnection = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(true);

    private final MultiplePacketQueue skriptQueue = new MultiplePacketQueue();

    private final String forcedName;

    public ClientConnection(ConnectionManager<T> connectionManager, SocketAddress socketAddressIn) {

        this.connectionManager = connectionManager;
        this.pluginInstance = connectionManager.getPluginInstance();
        this.socketAddress = socketAddressIn;
        this.packetHandlerManager = new PacketHandlerManager(this);

        this.forcedName = pluginInstance.getConfig().getString("forced-connection-name");

        this.clientInfo = new ClientInfo(pluginInstance.getServer().getMaxPlayers(), pluginInstance.getServer().getPort(),
                pluginInstance.getServer().getVersion());

    }

    public void start() {
        pluginInstance.getTaskManager().newAsyncTask(() -> clientCommHandler.dataOutLoop());
        pluginInstance.getTaskManager().newAsyncTask(() -> clientCommHandler.dataInLoop());
        pluginInstance.getTaskManager().newAsyncTask(() -> clientCommHandler.processLoop());
        pluginInstance.getTaskManager().newAsyncTask(this::handshakeTask);
    }

    public abstract void onRegister();

    private void handshakeTask() {
        clientCommHandler.awaitInitialConnection();
        if (forcedName.isEmpty()) {
            sendDirect(new HandshakePacket(PacketTypes.PROXY_CLIENT_INFO, clientInfo));
        } else {
            sendDirect(new HandshakePacket(PacketTypes.CONNECTION_NAME, new Object[]{forcedName, clientInfo}));
        }
        sendDirect(new HandshakePacket(PacketTypes.GLOBAL_SCRIPT, new Object[0]));
    }

    @Override
    public void consume(Packet packetIn) {
        clientCommHandler.awaitInitialConnection();
        if (connected.compareAndSet(false, true)) {
            connectionManager.register(this);
            onRegister();
            connected.set(true);
        }//
        if (packetIn.shouldHandle()) {
            packetHandlerManager.handlePacket(packetIn);
        } else {
            SinglePacketQueue multiplePacketQueue = requests.get(packetIn.getId());
            if (multiplePacketQueue != null) {
                multiplePacketQueue.enqueue(packetIn);
            }
        }
    }

    public Optional<Packet> waitForRequest(Packet packetIn) {
        UUID packetId = packetIn.getId();
        SinglePacketQueue requestQueue = new SinglePacketQueue();
        requests.put(packetId, requestQueue);
        Optional<Packet> request = requestQueue.take(true);
        requests.remove(packetId);
        return request;
    }

    public boolean sendDirect(Packet packetIn) {
        if (properConnection.get() || packetIn instanceof HandshakePacket) {
            clientCommHandler.enqueuePacket(packetIn);
            pluginInstance.logDebug("Sent packet " + packetIn.getType() + "...");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void destroyConsumer() {
        destroy();
    }

    public void destroy() {
        if (running.compareAndSet(true, false)) {
            connected.set(false);
            skriptQueue.notifyShutdown();
            clientCommHandler.destroy();
            connectionManager.deRegister(this);
        }
    }

    protected void setClientCommHandler(ClientCommHandler<T> handlerIn) {
        this.clientCommHandler = handlerIn;
        clientCommHandler.setConnection(this);
    }

    public void signifyProperConnection() {
        properConnection.set(true);
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
