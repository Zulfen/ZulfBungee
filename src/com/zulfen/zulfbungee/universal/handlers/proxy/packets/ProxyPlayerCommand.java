package com.zulfen.zulfbungee.universal.handlers.proxy.packets;

import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;

import java.util.Optional;

public class ProxyPlayerCommand<P, T> extends PacketHandler<P, T> {

    public ProxyPlayerCommand(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connection) {

        ClientPlayerDataContainer playerDataContainer = (ClientPlayerDataContainer) packetIn.getDataSingle();

        for (ClientPlayer clientPlayer : playerDataContainer.getPlayers()) {

            Optional<ZulfProxyPlayer<P, T>> player = getProxy().getPlayer(clientPlayer);

            if (player.isPresent()) {
                Optional<ProxyServerConnection<P, T>> serverConnection = getMainServer().getConnection(player.get());
                serverConnection.ifPresent(pBaseServerConnection -> pBaseServerConnection.sendDirect(packetIn));
            }
        }

        return null;

    }
}
