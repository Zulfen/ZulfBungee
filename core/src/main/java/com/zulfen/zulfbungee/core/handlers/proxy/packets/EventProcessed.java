package com.zulfen.zulfbungee.core.handlers.proxy.packets;

import com.zulfen.zulfbungee.core.handlers.PacketHandler;
import com.zulfen.zulfbungee.core.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.core.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.client.ProxyEventResponse;

public class EventProcessed<P, T, C> extends PacketHandler<P, T, C> {

    public EventProcessed(PacketHandlerManager<P, T, C> packetHandlerManagerIn) {
        super(packetHandlerManagerIn);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> connection) {
        ProxyEventResponse response = (ProxyEventResponse) packetIn;
        return packetIn.response(false, false, getMainServer().hasEventProcessed(response.getEventId()));
    }
}
