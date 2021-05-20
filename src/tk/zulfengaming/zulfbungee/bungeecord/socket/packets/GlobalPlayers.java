package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.zulfengaming.zulfbungee.bungeecord.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.utilclasses.skript.ProxyPlayer;

import java.net.SocketAddress;
import java.util.Collection;

public class GlobalPlayers extends PacketHandler {

    public GlobalPlayers(Server serverIn) {
        super(serverIn, PacketTypes.GLOBAL_PLAYERS);

    }

    // not implemented
    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        final Collection<ProxiedPlayer> players = getProxy().getPlayers();

        ProxyPlayer[] convertedPlayers = players.stream()
                .map(proxiedPlayer -> new ProxyPlayer(proxiedPlayer.getName(), proxiedPlayer.getUniqueId()))
                .toArray(ProxyPlayer[]::new);

        return new Packet(PacketTypes.GLOBAL_PLAYERS, true, false, convertedPlayers);

    }
}
