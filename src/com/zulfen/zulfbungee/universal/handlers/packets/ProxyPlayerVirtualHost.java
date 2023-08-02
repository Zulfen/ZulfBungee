package com.zulfen.zulfbungee.universal.handlers.packets;

import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

public class ProxyPlayerVirtualHost<P, T> extends PacketHandler<P, T> {

    public ProxyPlayerVirtualHost(PacketHandlerManager<P, T> packetHandlerManagerIn) {
        super(packetHandlerManagerIn);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connection) {

        ClientPlayer playerIn = (ClientPlayer) packetIn.getDataSingle();
        Optional<ZulfProxyPlayer<P, T>> playerOptional = getProxy().getPlayer(playerIn);

        if (playerOptional.isPresent()) {
            Optional<InetSocketAddress> virtHostOptional = playerOptional.get().getVirtualHost();
            if (virtHostOptional.isPresent()) {
                String virtualHostString = virtHostOptional.get().getAddress().toString();
                return new Packet(PacketTypes.PLAYER_VIRTUAL_HOST, false, false, virtualHostString);
            }
        }

        return new Packet(PacketTypes.PLAYER_VIRTUAL_HOST, false, false, new Object[0]);

    }

}
