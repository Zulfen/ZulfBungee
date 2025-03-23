package com.zulfen.zulfbungee.core.handlers;

import com.zulfen.zulfbungee.core.managers.MainServer;
import com.zulfen.zulfbungee.core.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.core.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.ZulfProxyImpl;

public abstract class PacketHandler<P, T, C> {

    private final PacketHandlerManager<P, T, C> packetHandlerManager;

    public abstract Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> connection);

    public MainServer<P, T, C> getMainServer() {
        return packetHandlerManager.getMainServer();
    }

    public ZulfProxyImpl<P, T, C> getProxy() {
        return packetHandlerManager.getMainServer().getImpl();
    }

    public PacketHandler(PacketHandlerManager<P, T, C> packetHandlerManagerIn) {
        this.packetHandlerManager = packetHandlerManagerIn;
    }

}
