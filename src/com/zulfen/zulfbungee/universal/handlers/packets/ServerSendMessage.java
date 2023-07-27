package com.zulfen.zulfbungee.universal.handlers.packets;

import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ServerMessage;
import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;

import java.util.Optional;

public class ServerSendMessage<P, T> extends PacketHandler<P, T> {

    public ServerSendMessage(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> address) {

        ServerMessage message = (ServerMessage) packetIn.getDataSingle();

        for (ClientServer server : message.getServers()) {

            String serverName = server.getName();

            Optional<ProxyServerConnection<P, T>> connectionFromName = getMainServer().getConnection(serverName);

            connectionFromName.ifPresent(pBaseServerConnection -> pBaseServerConnection
                    .sendDirect(new Packet(PacketTypes.SERVER_SEND_MESSAGE_EVENT, false, true, message)));

        }

        return null;
    }
}
