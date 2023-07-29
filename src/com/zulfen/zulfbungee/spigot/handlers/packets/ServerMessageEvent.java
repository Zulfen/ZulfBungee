package com.zulfen.zulfbungee.spigot.handlers.packets;

import com.zulfen.zulfbungee.spigot.interfaces.PacketHandler;
import com.zulfen.zulfbungee.spigot.event.events.EventProxyMessage;
import com.zulfen.zulfbungee.spigot.socket.Connection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ServerMessage;

public class ServerMessageEvent extends PacketHandler {

    public ServerMessageEvent(Connection<?> connectionIn) {
        super(connectionIn, true, PacketTypes.SERVER_SEND_MESSAGE_EVENT);

    }

    @Override
    public void handlePacket(Packet packetIn) {

        ServerMessage message = (ServerMessage) packetIn.getDataSingle();

        getConnection().getPluginInstance().getServer().getPluginManager().callEvent(new EventProxyMessage(message));

    }
}
