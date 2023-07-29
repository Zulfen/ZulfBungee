package com.zulfen.zulfbungee.spigot.handlers.packets;

import com.zulfen.zulfbungee.spigot.interfaces.PacketHandler;
import com.zulfen.zulfbungee.spigot.event.events.EventPlayerServerConnect;
import com.zulfen.zulfbungee.spigot.socket.Connection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;

public class PlayerConnectEvent extends PacketHandler {

    public PlayerConnectEvent(Connection<?> connectionIn) {
        super(connectionIn, true, PacketTypes.CONNECT_EVENT);

    }

    @Override
    public void handlePacket(Packet packetIn) {

        ClientPlayer player = (ClientPlayer) packetIn.getDataSingle();

        if (player != null) {
            getConnection().getPluginInstance().getServer().getPluginManager().callEvent(
                    new EventPlayerServerConnect(player)
            );
        }

    }
}
