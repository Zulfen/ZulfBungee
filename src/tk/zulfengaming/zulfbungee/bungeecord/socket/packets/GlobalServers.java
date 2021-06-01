package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.net.SocketAddress;

public class GlobalServers extends PacketHandler {

    public GlobalServers(Server serverIn) {
        super(serverIn, PacketTypes.GLOBAL_SERVERS);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        return new Packet(PacketTypes.GLOBAL_SERVERS,
                false,
                false,
                getMainServer().getServers().values().toArray(new ProxyServer[0]));
    }
}