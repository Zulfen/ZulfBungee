package com.zulfen.zulfbungee.universal.handlers.proxy.packets;

import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;

import java.util.Optional;

public class PlayerServer<P, T, C> extends PacketHandler<P, T, C> {

    public PlayerServer(PacketHandlerManager<P, T, C> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> connection) {

        ClientPlayer playerIn = (ClientPlayer) packetIn.getDataSingle();
        Optional<ZulfProxyPlayer<P, T, C>> player = getProxy().getPlayer(playerIn);

        if (player.isPresent()) {

            ZulfProxyServer<P, T, C> server = player.get().getServer();
            String name = server.getName();

            return packetIn.response(false, false, name);

        } else {
            return packetIn.response(false, false);
        }


    }
}