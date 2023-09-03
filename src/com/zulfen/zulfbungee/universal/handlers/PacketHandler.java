package com.zulfen.zulfbungee.universal.handlers;

import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.ZulfProxyImpl;

public abstract class PacketHandler<P, T> {

    private final PacketHandlerManager<P, T> packetHandlerManager;

    public abstract Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connection);

    public MainServer<P, T> getMainServer() {
        return packetHandlerManager.getMainServer();
    }

    public ZulfProxyImpl<P, T> getProxy() {
        return packetHandlerManager.getMainServer().getImpl();
    }

    public PacketHandler(PacketHandlerManager<P, T> packetHandlerManagerIn) {
        this.packetHandlerManager = packetHandlerManagerIn;
    }

}
