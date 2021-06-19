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
import java.util.stream.Stream;

public class ProxyPlayers extends PacketHandler {

    public ProxyPlayers(Server serverIn) {
        super(serverIn, PacketTypes.PROXY_PLAYERS);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        LinkedList<ProxyPlayer> playersOut = new LinkedList<>();

        if (packetIn.getDataArray() != null) {

            ProxyServer[] servers = Stream.of(packetIn.getDataArray())
                    .filter(ProxyServer.class::isInstance)
                    .map(ProxyServer.class::cast)
                    .toArray(ProxyServer[]::new);

            for (ProxyServer server : servers) {

                ServerConnection connection = getMainServer().getActiveConnections().get(server.getName());

                for (ProxyPlayer player : connection.playerList()) {
                    playersOut.addLast(player);
                }

            }


        } else {

            for (ServerConnection connection : getMainServer().getActiveConnections().values()) {
                for (ProxyPlayer player : connection.playerList()) {
                    playersOut.addLast(player);
                }
            }

        }


        return new Packet(PacketTypes.PROXY_PLAYERS, false, false, playersOut.toArray(new ProxyPlayer[0]));

    }
}
