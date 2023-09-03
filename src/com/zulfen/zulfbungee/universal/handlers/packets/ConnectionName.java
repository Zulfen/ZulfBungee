package com.zulfen.zulfbungee.universal.handlers.packets;

import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientInfo;

public class ConnectionName<P, T> extends PacketHandler<P, T> {

    public ConnectionName(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connection) {

        String givenName = (String) packetIn.getDataArray()[0];
        ClientInfo clientInfo = (ClientInfo) packetIn.getDataArray()[1];

        getMainServer().addActiveConnection(connection, givenName, clientInfo);
        return new Packet(PacketTypes.CONNECTION_NAME, true, true, givenName);

    }

}
