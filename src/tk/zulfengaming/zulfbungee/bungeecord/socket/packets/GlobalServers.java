package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.ServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
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

            ServerConnection connection = entry.getValue();

            servers.addLast(new ProxyServer(entry.getKey(), connection.getClientInfo()));

        }

        return new Packet(PacketTypes.GLOBAL_SERVERS,
                false,
                false,
                servers.toArray(new ProxyServer[0]));
    }
}