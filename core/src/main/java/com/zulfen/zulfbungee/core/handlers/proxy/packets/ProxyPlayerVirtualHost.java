package com.zulfen.zulfbungee.core.handlers.proxy.packets;

import com.zulfen.zulfbungee.core.handlers.PacketHandler;
import com.zulfen.zulfbungee.core.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.core.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.core.socket.objects.proxy.ZulfProxyPlayer;

import java.net.InetSocketAddress;
import java.util.Optional;

public class ProxyPlayerVirtualHost<P, T, C> extends PacketHandler<P, T, C> {

    public ProxyPlayerVirtualHost(PacketHandlerManager<P, T, C> packetHandlerManagerIn) {
        super(packetHandlerManagerIn);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> connection) {

        ClientPlayer playerIn = (ClientPlayer) packetIn.getDataSingle();
        Optional<ZulfProxyPlayer<P, T, C>> playerOptional = getProxy().getPlayer(playerIn);

        if (playerOptional.isPresent()) {
            Optional<InetSocketAddress> virtHostOptional = playerOptional.get().getVirtualHost();
            if (virtHostOptional.isPresent()) {
                String virtualHostString = virtHostOptional.get().getAddress().toString();
                return packetIn.response( false, false, virtualHostString);
            }
        }

        return packetIn.response(false, false);

    }

}
