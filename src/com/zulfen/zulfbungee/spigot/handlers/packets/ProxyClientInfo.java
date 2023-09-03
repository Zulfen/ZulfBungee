package com.zulfen.zulfbungee.spigot.handlers.packets;

import com.zulfen.zulfbungee.spigot.interfaces.PacketHandler;
import com.zulfen.zulfbungee.spigot.socket.ClientConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientInfo;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProxyClientInfo extends PacketHandler {

    public ProxyClientInfo(ClientConnection<?> connectionIn) {
        super(connectionIn, false, PacketTypes.PROXY_CLIENT_INFO);

    }

    @Override
    public void handlePacket(Packet packetIn) {

        Map<String, ClientInfo> serverMap = Stream.of(packetIn.getDataArray())
                .filter(ClientServer.class::isInstance)
                .map(ClientServer.class::cast)
                .collect(Collectors.toMap(
                        ClientServer::getName,
                        ClientServer::getClientInfo
                ));

        getConnection().getPluginInstance().getConnectionManager().setProxyServers(serverMap);

    }
}
