package com.zulfen.zulfbungee.core.handlers.proxy.packets;

import com.zulfen.zulfbungee.core.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.core.handlers.PacketHandler;
import com.zulfen.zulfbungee.core.managers.PacketHandlerManager;

import java.util.*;
import java.util.stream.Stream;

public class ProxyPlayers<P, T, C> extends PacketHandler<P, T, C> {

    public ProxyPlayers(PacketHandlerManager<P, T, C> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> connectionIn) {

        ClientPlayer[] playersOut;

        if (packetIn.getDataArray().length != 0) {

            Stream<ClientServer> servers = Stream.of(packetIn.getDataArray())
                    .filter(Objects::nonNull)
                    .filter(ClientServer.class::isInstance)
                    .map(ClientServer.class::cast);

            playersOut = servers.map(server -> getProxy().getServer(server))
                    .flatMap(Optional::stream) // Unwraps non-empty Optionals
                    .flatMap(proxyServer -> proxyServer.getPlayers().stream())
                    .map(player -> getMainServer().toClientPlayer(player))
                    .flatMap(Optional::stream) // Unwraps non-empty Optionals
                    .toArray(ClientPlayer[]::new);


        } else {

            playersOut = getProxy().getAllPlayers().stream()
                    .map(proxyPlayer -> getMainServer().toClientPlayer(proxyPlayer))
                    .flatMap(Optional::stream)
                    .toArray(ClientPlayer[]::new);


        }


        return packetIn.response(false, false, playersOut);

    }
}
