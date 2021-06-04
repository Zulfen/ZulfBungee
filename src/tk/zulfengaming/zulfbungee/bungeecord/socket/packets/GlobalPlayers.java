package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.ServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;

import java.net.SocketAddress;
import java.util.LinkedList;

public class GlobalPlayers extends PacketHandler {

    public GlobalPlayers(Server serverIn) {
        super(serverIn, PacketTypes.GLOBAL_PLAYERS);

    }

    // not implemented
    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        LinkedList<ProxyPlayer> playersOut = new LinkedList<>();

        for (ServerConnection connection : getMainServer().getActiveConnections().values()) {
            for (ProxyPlayer player : connection.getPlayers().values()) {
                playersOut.addLast(player);
            }
        }

        return new Packet(PacketTypes.GLOBAL_PLAYERS, false, false, playersOut.toArray(new ProxyPlayer[0]));

    }
}
