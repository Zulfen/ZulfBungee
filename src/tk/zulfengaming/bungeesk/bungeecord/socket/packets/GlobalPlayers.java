package tk.zulfengaming.bungeesk.bungeecord.socket.packets;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.zulfengaming.bungeesk.bungeecord.handlers.PacketHandler;
import tk.zulfengaming.bungeesk.bungeecord.socket.Server;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

public class GlobalPlayers extends PacketHandler {

    public GlobalPlayers(Server serverIn) {
        super(serverIn, PacketTypes.GLOBAL_PLAYERS);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        final Collection<ProxiedPlayer> players = getProxy().getPlayers();
        final ArrayList<String> playersStrings = new ArrayList<>();

        for (ProxiedPlayer player : players.stream().parallel().collect(Collectors.toList())) {
            playersStrings.add(player.getName());
        }


        return new Packet(address, getProxy().getName(), PacketTypes.GLOBAL_PLAYERS, false, playersStrings);

    }
}
