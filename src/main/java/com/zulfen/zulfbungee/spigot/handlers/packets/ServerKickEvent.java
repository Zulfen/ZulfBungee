package com.zulfen.zulfbungee.spigot.handlers.packets;

import com.zulfen.zulfbungee.spigot.interfaces.PacketHandler;
import com.zulfen.zulfbungee.spigot.event.events.EventPlayerServerKick;
import com.zulfen.zulfbungee.spigot.socket.ClientConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;

public class ServerKickEvent extends PacketHandler {

    public ServerKickEvent(ClientConnection<?> connectionIn) {
        super(connectionIn, true, PacketTypes.KICK_EVENT);

    }

    @Override
    public void handlePacket(Packet packetIn) {

        ClientPlayerDataContainer container = (ClientPlayerDataContainer) packetIn.getDataSingle();

        String reason = (String) container.getDataSingle();
        ClientPlayer player = container.getPlayers()[0];

        getConnection().getPluginInstance().getServer().getPluginManager().callEvent(
                new EventPlayerServerKick(reason, player)
        );

    }
}
