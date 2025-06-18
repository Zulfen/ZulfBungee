package com.zulfen.zulfbungee.spigot.handlers.packets;

import com.zulfen.zulfbungee.core.socket.objects.proxy.ProxyEventPacket;
import com.zulfen.zulfbungee.spigot.interfaces.PacketHandler;
import com.zulfen.zulfbungee.spigot.event.events.EventPlayerServerDisconnect;
import com.zulfen.zulfbungee.spigot.socket.ClientConnection;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.core.socket.objects.client.skript.ClientPlayerDataContainer;

public class PlayerDisconnectEvent extends PacketHandler {

    public PlayerDisconnectEvent(ClientConnection<?> connectionIn) {
        super(connectionIn, true, PacketTypes.DISCONNECT_EVENT);

    }

    @Override
    public void handlePacket(Packet packetIn) {

        ProxyEventPacket eventPacket = (ProxyEventPacket) packetIn;
        ClientPlayerDataContainer dataContainer = (ClientPlayerDataContainer) eventPacket.getDataSingle();

        getConnection().getPluginInstance().getServer().getPluginManager().callEvent(
                new EventPlayerServerDisconnect(
                        eventPacket.getEventId(),
                        dataContainer.getPlayers()[0],
                        (ClientServer) dataContainer.getDataSingle()
                )
        );


    }
}
