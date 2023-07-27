package com.zulfen.zulfbungee.spigot.socket.packets;

import com.zulfen.zulfbungee.spigot.interfaces.PacketHandler;
import com.zulfen.zulfbungee.spigot.event.events.EventPlayerSwitchServer;
import com.zulfen.zulfbungee.spigot.socket.Connection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;

public class ServerSwitchEvent extends PacketHandler {

    public ServerSwitchEvent(Connection connectionIn) {
        super(connectionIn, true, PacketTypes.SERVER_SWITCH_EVENT);

    }

    @Override
    public void handlePacket(Packet packetIn) {

        ClientPlayerDataContainer dataContainer = (ClientPlayerDataContainer) packetIn.getDataSingle();
        ClientServer fromServer = (ClientServer) dataContainer.getDataSingle();

        getConnection().getPluginInstance().getServer().getPluginManager().callEvent(
                new EventPlayerSwitchServer(fromServer, dataContainer.getPlayers()[0])
        );

    }
}
