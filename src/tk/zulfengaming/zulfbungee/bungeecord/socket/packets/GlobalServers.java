package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.ServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.net.SocketAddress;
import java.util.LinkedList;
import java.util.Map;

public class GlobalServers extends PacketHandler {

    public GlobalServers(Server serverIn) {
        super(serverIn, PacketTypes.GLOBAL_SERVERS);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        LinkedList<ProxyServer> servers = new LinkedList<>();

        for (Map.Entry<String, ServerConnection> entry : getMainServer().getActiveConnections().entrySet()) {
            servers.addLast(new ProxyServer(entry.getKey(), entry.getValue().getPlayers().values().toArray(new ProxyPlayer[0])));
        }

        return new Packet(PacketTypes.GLOBAL_SERVERS,
                false,
                false,
                servers.toArray(new ProxyServer[0]));
    }
}