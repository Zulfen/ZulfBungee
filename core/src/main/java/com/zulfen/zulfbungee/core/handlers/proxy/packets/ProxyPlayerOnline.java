package com.zulfen.zulfbungee.core.handlers.proxy.packets;

import com.zulfen.zulfbungee.core.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.core.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.core.handlers.PacketHandler;
import com.zulfen.zulfbungee.core.managers.PacketHandlerManager;

import java.util.Optional;

public class ProxyPlayerOnline<P, T, C> extends PacketHandler<P, T, C> {

    public ProxyPlayerOnline(PacketHandlerManager<P, T, C> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> address) {

        ClientPlayer skriptPlayer = (ClientPlayer) packetIn.getDataSingle();

        if (skriptPlayer != null) {

            Optional<ZulfProxyPlayer<P, T, C>> proxiedPlayer = getProxy().getPlayer(skriptPlayer);
            if (proxiedPlayer.isPresent()) {
                return packetIn.response(false, false, true);
            }

        }

        return packetIn.response(false, false, false);
    }
}