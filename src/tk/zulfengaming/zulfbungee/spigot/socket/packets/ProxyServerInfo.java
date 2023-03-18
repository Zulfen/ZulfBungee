package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.managers.ConnectionManager;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.IncomingServerType;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientInfo;

import java.net.SocketAddress;
import java.util.stream.Stream;

public class ProxyServerInfo extends PacketHandler {

    public ProxyServerInfo(Connection connectionIn) {
        super(connectionIn, false, PacketTypes.PROXY_CLIENT_INFO);

    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        ClientServer[] servers = Stream.of(packetIn.getDataArray())
                .filter(ClientServer.class::isInstance)
                .toArray(ClientServer[]::new);

        getConnection().getConnectionManager().setProxyServers(servers);

    }
}
