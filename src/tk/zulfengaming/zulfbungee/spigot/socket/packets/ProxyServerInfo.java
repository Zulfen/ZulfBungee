package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientInfo;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProxyServerInfo extends PacketHandler {

    public ProxyServerInfo(Connection connectionIn) {
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

        getConnection().getConnectionManager().setProxyServers(serverMap);

    }
}
