package com.zulfen.zulfbungee.spigot.socket;

import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.interfaces.ClientCommHandler;
import com.zulfen.zulfbungee.spigot.managers.ConnectionManager;
import com.zulfen.zulfbungee.spigot.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientInfo;
import com.zulfen.zulfbungee.universal.socket.objects.client.HandshakePacket;
import com.zulfen.zulfbungee.universal.util.BlockingPacketQueue;

import java.net.SocketAddress;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ClientConnection<T> {

    protected final ConnectionManager<T> connectionManager;
    protected final ZulfBungeeSpigot pluginInstance;
    protected final PacketHandlerManager packetHandlerManager;
    protected ClientCommHandler<T> clientCommHandler;

    protected final ClientInfo clientInfo;
    protected final SocketAddress socketAddress;

    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicBoolean properConnection = new AtomicBoolean(false);
    private final AtomicBoolean running = new AtomicBoolean(true);

    protected final BlockingPacketQueue queueIn = new BlockingPacketQueue();
    protected final BlockingPacketQueue queueOut = new BlockingPacketQueue();

    private final BlockingPacketQueue skriptQueue = new BlockingPacketQueue();

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
        pluginInstance.getTaskManager().newAsyncTask(this::dataInLoop);
        pluginInstance.getTaskManager().newAsyncTask(this::dataOutLoop);
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

    private void dataOutLoop() {
        while (running.get()) {
            Optional<Packet> take = queueOut.take(false);
            take.ifPresent(packet -> clientCommHandler.writePacket(packet));
        }
    }

    private void dataInLoop() {

        clientCommHandler.awaitInitialConnection();
        if (running.get()) {

            connectionManager.register(this);
            onRegister();
            connected.set(true);

            while (connected.get()) {

                Optional<Packet> read = clientCommHandler.readPacket();

                if (read.isPresent()) {

                    Packet packet = read.get();

                    if (packet.shouldHandle()) {
                        packetHandlerManager.handlePacket(packet);
                    } else {
                        skriptQueue.offer(packet);

                    }


                } else {
                    skriptQueue.notifyListeners();
                }

            }
        }

    }

    public boolean sendDirect(Packet packetIn) {
        if (properConnection.get() || packetIn instanceof HandshakePacket) {
            queueOut.offer(packetIn);
            pluginInstance.logDebug("Sent packet " + packetIn.getType() + "...");
            return true;
        } else {
            return false;
        }
    }

    public Optional<Packet> readSkriptQueue() {

        if (properConnection.get()) {
            return skriptQueue.take(true);
        }

        return Optional.empty();

    }

    public void destroy() {
        running.set(false);
        connected.set(false);
        queueIn.notifyListeners();
        queueOut.notifyListeners();
        skriptQueue.notifyListeners();
        clientCommHandler.destroy();
        connectionManager.deRegister(this);
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
