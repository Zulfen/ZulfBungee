package com.zulfen.zulfbungee.core.handlers.proxy.packets;

import com.zulfen.zulfbungee.core.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.core.socket.objects.client.skript.ClientPlayerDataContainer;
import com.zulfen.zulfbungee.core.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.core.handlers.PacketHandler;
import com.zulfen.zulfbungee.core.managers.PacketHandlerManager;

import java.util.Optional;

public class PlayerSendMessage<P, T, C> extends PacketHandler<P, T, C> {

    public PlayerSendMessage(PacketHandlerManager<P, T, C> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> address) {

        ClientPlayerDataContainer dataContainer = (ClientPlayerDataContainer) packetIn.getDataSingle();

        for (ClientPlayer clientPlayer : dataContainer.getPlayers()) {

            Optional<ZulfProxyPlayer<P, T, C>> getProxyPlayer = getProxy().getPlayer(clientPlayer);

            getProxyPlayer.ifPresent(pZulfProxyPlayer -> {
                Optional<ProxyServerConnection<P, T, C>> getConnection = getMainServer().getConnection(pZulfProxyPlayer);
                getConnection.ifPresent(pBaseServerConnection -> pBaseServerConnection.sendDirect(packetIn));
            });


        }


        return null;

    }
}
