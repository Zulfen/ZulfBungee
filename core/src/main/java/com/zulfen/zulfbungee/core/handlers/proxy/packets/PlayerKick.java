package com.zulfen.zulfbungee.core.handlers.proxy.packets;

import com.zulfen.zulfbungee.core.handlers.PacketHandler;
import com.zulfen.zulfbungee.core.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.core.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.core.socket.objects.client.skript.ClientPlayerDataContainer;
import com.zulfen.zulfbungee.core.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;

public class PlayerKick<P, T, C> extends PacketHandler<P, T, C> {

    public PlayerKick(PacketHandlerManager<P, T, C> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> connection) {

        ClientPlayerDataContainer container = (ClientPlayerDataContainer) packetIn.getDataSingle();
        String message = (String) container.getDataSingle();

        for (ClientPlayer player : container.getPlayers()) {
            Optional<ZulfProxyPlayer<P, T, C>> proxyPlayer = getProxy().getPlayer(player);
            proxyPlayer.ifPresent(pZulfProxyPlayer -> pZulfProxyPlayer.disconnect(message));
        }

        return null;

    }
}