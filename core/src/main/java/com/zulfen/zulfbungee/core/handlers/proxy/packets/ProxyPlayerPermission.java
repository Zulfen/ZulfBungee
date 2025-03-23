package com.zulfen.zulfbungee.core.handlers.proxy.packets;

import com.zulfen.zulfbungee.core.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.core.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.core.socket.objects.client.skript.ClientPlayerDataContainer;
import com.zulfen.zulfbungee.core.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.core.handlers.PacketHandler;

import java.util.Optional;
import java.util.stream.Stream;

public class ProxyPlayerPermission<P, T, C> extends PacketHandler<P, T, C> {

    public ProxyPlayerPermission(PacketHandlerManager<P, T, C> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> address) {

        ClientPlayerDataContainer dataContainer = (ClientPlayerDataContainer) packetIn.getDataSingle();

        ClientPlayer clientPlayer = dataContainer.getPlayers()[0];
        Optional<ZulfProxyPlayer<P, T, C>> getPlayer = getProxy().getPlayer(clientPlayer);

        if (getPlayer.isPresent()) {

            ZulfProxyPlayer<P, T, C> proxyPlayer = getPlayer.get();

            boolean hasPermissions = Stream.of(dataContainer.getDataArray())
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .allMatch(proxyPlayer::hasPermission);

            return packetIn.response(true, false, hasPermissions);


        }

        return packetIn.response(true, false, false);

    }

}