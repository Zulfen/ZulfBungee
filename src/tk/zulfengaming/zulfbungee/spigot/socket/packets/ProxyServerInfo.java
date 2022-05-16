package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.handlers.ProxyServerInfoManager;
import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.net.SocketAddress;
import java.util.stream.Stream;

public class ProxyServerInfo extends PacketHandler {

    public ProxyServerInfo(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.PROXY_SERVER_INFO);

    }

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        ProxyServer[] serversIn = Stream.of(packetIn.getDataArray())
                .filter(ProxyServer.class::isInstance)
                .map(ProxyServer.class::cast)
                .toArray(ProxyServer[]::new);

        ProxyServerInfoManager.setServers(serversIn);

    }
}
