package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.utilclasses.skript.ProxyServer;

import java.net.SocketAddress;
import java.util.Collection;

public class GlobalServers extends PacketHandler {

    public GlobalServers(Server serverIn) {
        super(serverIn, PacketTypes.GLOBAL_SERVERS);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        Collection<String> activeConnections = getMainServer().getActiveConnections().keySet();

        return new Packet(PacketTypes.GLOBAL_SERVERS, false, false,
                activeConnections.stream()
                .map(ProxyServer::new)
                .toArray(ProxyServer[]::new)
        );
    }
}