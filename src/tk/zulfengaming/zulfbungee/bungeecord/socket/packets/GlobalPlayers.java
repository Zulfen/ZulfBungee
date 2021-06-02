package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.ServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;

import java.net.SocketAddress;

public class GlobalPlayers extends PacketHandler {

    public GlobalPlayers(Server serverIn) {
        super(serverIn, PacketTypes.GLOBAL_PLAYERS);

    }

    // not implemented
    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        ServerConnection connection = getMainServer().getServerConnections().get(address);

        return new Packet(PacketTypes.GLOBAL_PLAYERS, false, false, connection.getPlayers().values().toArray(new ProxyPlayer[0]));

    }
}
