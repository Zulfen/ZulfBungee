package com.zulfen.zulfbungee.universal.handlers.packets;

import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;

// used when you only have the player's name but not the uuid
public class ProxyPlayerUUID<P, T> extends PacketHandler<P, T> {

    public ProxyPlayerUUID(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connectionIn) {

        String playerName = (String) packetIn.getDataSingle();
        Optional<ZulfProxyPlayer<P, T>> proxyPlayerOptional = getProxy().getPlayer(playerName);

       if (proxyPlayerOptional.isPresent()) {
           ZulfProxyPlayer<P, T> proxyPlayer = proxyPlayerOptional.get();
           Optional<ClientPlayer> clientPlayerOptional = getMainServer().toClientPlayer(proxyPlayer);
           if (clientPlayerOptional.isPresent()) {
               return new Packet(PacketTypes.PROXY_PLAYER_UUID, false, false, clientPlayerOptional.get());
           }
       }

       return new Packet(PacketTypes.PROXY_PLAYER_UUID, false, false, new Object[0]);

    }
}