package com.zulfen.zulfbungee.universal.handlers.packets;

import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;

public class ClientDisconnect<P, T> extends PacketHandler<P, T> {

    public ClientDisconnect(PacketHandlerManager<P, T> packetHandlerManagerIn) {
        super(packetHandlerManagerIn);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connection) {
        connection.destroy();
        return null;
    }
}
