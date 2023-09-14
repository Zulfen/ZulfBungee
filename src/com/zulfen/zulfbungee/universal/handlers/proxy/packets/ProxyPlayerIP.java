package com.zulfen.zulfbungee.universal.handlers.proxy.packets;

import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;

public class ProxyPlayerIP<P, T> extends PacketHandler<P, T> {

    public ProxyPlayerIP(PacketHandlerManager<P, T> packetHandlerManagerIn) {
        super(packetHandlerManagerIn);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connection) {

        ClientPlayer playerIn = (ClientPlayer) packetIn.getDataSingle();
        Optional<ZulfProxyPlayer<P, T>> playerOptional = getProxy().getPlayer(playerIn);
        if (playerOptional.isPresent()) {
            String stringAddress = playerOptional.get().getSocketAddress().getAddress().toString();
            return new Packet(PacketTypes.PROXY_PLAYER_IP, false, false, stringAddress);
        } else {
            return new Packet(PacketTypes.PROXY_PLAYER_IP, false, false, new Object[0]);
        }

    }

}
