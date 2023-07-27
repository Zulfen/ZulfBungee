package com.zulfen.zulfbungee.universal.handlers.packets;

import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProxyPlayers<P, T> extends PacketHandler<P, T> {

    public ProxyPlayers(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connectionIn) {

        ArrayList<ClientPlayer> playersOut = new ArrayList<>();

        if (packetIn.getDataArray().length != 0) {

            ClientServer[] servers = Stream.of(packetIn.getDataArray())
                    .filter(Objects::nonNull)
                    .filter(ClientServer.class::isInstance)
                    .map(ClientServer.class::cast)
                    .toArray(ClientServer[]::new);

            for (ClientServer server : servers) {

                Optional<ZulfProxyServer<P, T>> zulfProxyServer = getProxy().getServer(server);

                if (zulfProxyServer.isPresent()) {
                    List<ZulfProxyPlayer<P, T>> players = zulfProxyServer.get().getPlayers();
                    for (ZulfProxyPlayer<P, T> player : players) {
                        Optional<ClientPlayer> clientPlayerOptional = getMainServer().toClientPlayer(player);
                        if (clientPlayerOptional.isPresent()) {
                            playersOut.add(clientPlayerOptional.get());
                        }
                    }
                }


            }


        } else {

            playersOut = getProxy().getAllPlayers().stream()
                    .map(proxyPlayer -> getMainServer().toClientPlayer(proxyPlayer))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toCollection(ArrayList::new));


        }


        return new Packet(PacketTypes.PROXY_PLAYERS, false, false, playersOut.toArray(new ClientPlayer[0]));

    }
}
