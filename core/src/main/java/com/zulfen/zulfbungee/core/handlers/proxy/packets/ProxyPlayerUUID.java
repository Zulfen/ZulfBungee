package com.zulfen.zulfbungee.core.handlers.proxy.packets;

import com.zulfen.zulfbungee.core.handlers.PacketHandler;
import com.zulfen.zulfbungee.core.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.core.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.core.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;

// used when you only have the player's name but not the uuid
public class ProxyPlayerUUID<P, T, C> extends PacketHandler<P, T, C> {

    public ProxyPlayerUUID(PacketHandlerManager<P, T, C> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> connectionIn) {

        String playerName = (String) packetIn.getDataSingle();
        Optional<ZulfProxyPlayer<P, T, C>> proxyPlayerOptional = getProxy().getPlayer(playerName);

       if (proxyPlayerOptional.isPresent()) {
           ZulfProxyPlayer<P, T, C> proxyPlayer = proxyPlayerOptional.get();
           Optional<ClientPlayer> clientPlayerOptional = getMainServer().toClientPlayer(proxyPlayer);
           if (clientPlayerOptional.isPresent()) {
               return packetIn.response(false, false, clientPlayerOptional.get());
           }
       }

       return packetIn.response( false, false);

    }
}