package com.zulfen.zulfbungee.universal.handlers;

import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.ZulfBungeeProxy;

public abstract class PacketHandler<P, T> {

    private final PacketHandlerManager<P, T> packetHandlerManager;

    public abstract Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connection);

    public MainServer<P, T> getMainServer() {
        return packetHandlerManager.getMainServer();
    }

    public ZulfBungeeProxy<P, T> getProxy() {
        return packetHandlerManager.getMainServer().getPluginInstance();
    }

    public PacketHandler(PacketHandlerManager<P, T> packetHandlerManagerIn) {
        this.packetHandlerManager = packetHandlerManagerIn;
    }

}
