package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;

import java.net.SocketAddress;
import java.util.stream.Stream;

public class ProxyServerInfo extends PacketHandler {

    public ProxyServerInfo(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.PROXY_CLIENT_INFO);

    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        ClientServer[] serversIn = Stream.of(packetIn.getDataArray())
                .filter(ClientServer.class::isInstance)
                .map(ClientServer.class::cast)
                .toArray(ClientServer[]::new);

        getConnection().setProxyServers(serversIn);

    }
}
