package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

public class ProxyPlayers extends PacketHandler {

    public ProxyPlayers(Server serverIn) {
        super(serverIn, PacketTypes.PROXY_PLAYERS);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection connectionIn) {

        ArrayList<ProxyPlayer> playersOut = new ArrayList<>();

        if (packetIn.getDataArray() != null) {

            ProxyServer[] servers = Stream.of(packetIn.getDataArray())
                    .filter(ProxyServer.class::isInstance)
                    .map(ProxyServer.class::cast)
                    .toArray(ProxyServer[]::new);

            for (ProxyServer server : servers) {

                Collection<ProxiedPlayer> players = getProxy().getServersCopy().get(server.getName()).getPlayers();

                for (ProxiedPlayer player : players) {
                    playersOut.add(new ProxyPlayer(player.getName(), player.getUniqueId()));
                }

            }


        } else {

            for (ProxiedPlayer player : getProxy().getPlayers()) {
                playersOut.add(new ProxyPlayer(player.getName(), player.getUniqueId()));
            }

        }


        return new Packet(PacketTypes.PROXY_PLAYERS, false, false, playersOut.toArray(new ProxyPlayer[0]));

    }
}
