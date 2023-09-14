package com.zulfen.zulfbungee.universal.handlers.proxy.packets;

import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;

import java.util.Optional;

public class PlayerSendMessage<P, T> extends PacketHandler<P, T> {

    public PlayerSendMessage(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> address) {

        ClientPlayerDataContainer dataContainer = (ClientPlayerDataContainer) packetIn.getDataSingle();

        for (ClientPlayer clientPlayer : dataContainer.getPlayers()) {

            Optional<ZulfProxyPlayer<P, T>> getProxyPlayer = getProxy().getPlayer(clientPlayer);

            getProxyPlayer.ifPresent(pZulfProxyPlayer -> {
                Optional<ProxyServerConnection<P, T>> getConnection = getMainServer().getConnection(pZulfProxyPlayer);
                getConnection.ifPresent(pBaseServerConnection -> pBaseServerConnection.sendDirect(packetIn));
            });


        }


        return null;

    }
}
