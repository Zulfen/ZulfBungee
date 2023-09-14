package com.zulfen.zulfbungee.universal.handlers.proxy.packets;

import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;

public class PlayerKick<P, T> extends PacketHandler<P, T> {

    public PlayerKick(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connection) {

        ClientPlayerDataContainer container = (ClientPlayerDataContainer) packetIn.getDataSingle();
        String message = (String) container.getDataSingle();

        for (ClientPlayer player : container.getPlayers()) {
            Optional<ZulfProxyPlayer<P, T>> proxyPlayer = getProxy().getPlayer(player);
            proxyPlayer.ifPresent(pZulfProxyPlayer -> pZulfProxyPlayer.disconnect(message));
        }

        return null;

    }
}