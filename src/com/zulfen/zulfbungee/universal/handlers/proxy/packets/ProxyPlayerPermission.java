package com.zulfen.zulfbungee.universal.handlers.proxy.packets;

import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.universal.handlers.PacketHandler;

import java.util.Optional;
import java.util.stream.Stream;

public class ProxyPlayerPermission<P, T> extends PacketHandler<P, T> {

    public ProxyPlayerPermission(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> address) {

        ClientPlayerDataContainer dataContainer = (ClientPlayerDataContainer) packetIn.getDataSingle();

        ClientPlayer clientPlayer = dataContainer.getPlayers()[0];
        Optional<ZulfProxyPlayer<P, T>> getPlayer = getProxy().getPlayer(clientPlayer);

        if (getPlayer.isPresent()) {

            ZulfProxyPlayer<P, T> proxyPlayer = getPlayer.get();

            boolean hasPermissions = Stream.of(dataContainer.getDataArray())
                    .filter(String.class::isInstance)
                    .map(String.class::cast)
                    .allMatch(proxyPlayer::hasPermission);

            return new Packet(PacketTypes.PROXY_PLAYER_PERMISSION, true, false, hasPermissions);


        }

        return new Packet(PacketTypes.PROXY_PLAYER_PERMISSION, true, false, false);

    }

}