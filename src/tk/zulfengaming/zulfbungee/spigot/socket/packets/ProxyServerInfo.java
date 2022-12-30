package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyServer;

import java.net.SocketAddress;
import java.util.stream.Stream;

public class ProxyServerInfo extends PacketHandler {

    public ProxyServerInfo(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.PROXY_CLIENT_INFO);

    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        ProxyServer[] serversIn = Stream.of(packetIn.getDataArray())
                .filter(ProxyServer.class::isInstance)
                .map(ProxyServer.class::cast)
                .toArray(ProxyServer[]::new);

        getConnection().setProxyServers(serversIn);

    }
}
