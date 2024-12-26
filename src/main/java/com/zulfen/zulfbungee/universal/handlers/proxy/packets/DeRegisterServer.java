package com.zulfen.zulfbungee.universal.handlers.proxy.packets;

import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;

public class DeRegisterServer<P, T, C> extends PacketHandler<P, T, C> {

    public DeRegisterServer(PacketHandlerManager<P, T, C> packetHandlerManagerIn) {
        super(packetHandlerManagerIn);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> connection) {

        String serverName = (String) packetIn.getDataArray()[0];

        if (getProxy().getServer(serverName).isPresent()) {
            getProxy().deRegisterServer(serverName);
        }

        return null;

    }

}
