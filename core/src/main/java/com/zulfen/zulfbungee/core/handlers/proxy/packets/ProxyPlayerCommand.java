package com.zulfen.zulfbungee.core.handlers.proxy.packets;

import com.zulfen.zulfbungee.core.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.core.socket.objects.client.skript.ClientPlayerDataContainer;
import com.zulfen.zulfbungee.core.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.core.handlers.PacketHandler;
import com.zulfen.zulfbungee.core.managers.PacketHandlerManager;

import java.util.Optional;

public class ProxyPlayerCommand<P, T, C> extends PacketHandler<P, T, C> {

    public ProxyPlayerCommand(PacketHandlerManager<P, T, C> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> connection) {

        ClientPlayerDataContainer playerDataContainer = (ClientPlayerDataContainer) packetIn.getDataSingle();

        for (ClientPlayer clientPlayer : playerDataContainer.getPlayers()) {

            Optional<ZulfProxyPlayer<P, T, C>> player = getProxy().getPlayer(clientPlayer);

            if (player.isPresent()) {
                Optional<ProxyServerConnection<P, T, C>> serverConnection = getMainServer().getConnection(player.get());
                serverConnection.ifPresent(pBaseServerConnection -> pBaseServerConnection.sendDirect(packetIn));
            }
        }

        return null;

    }
}
