package tk.zulfengaming.bungeesk.bungeecord.socket.packets;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.zulfengaming.bungeesk.bungeecord.handlers.PacketHandler;
import tk.zulfengaming.bungeesk.bungeecord.socket.Server;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.net.SocketAddress;
import java.util.Collection;
import java.util.UUID;

public class GlobalPlayers extends PacketHandler {

    public GlobalPlayers(Server serverIn) {
        super(serverIn, PacketTypes.GLOBAL_PLAYERS);

    }

    // not implemented
    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        final Collection<ProxiedPlayer> players = getProxy().getPlayers();

        // players.stream().map(ProxiedPlayer::getUniqueId).toArray()

        return new Packet(getProxy().getName(), PacketTypes.GLOBAL_PLAYERS, true, false, new Object[] {UUID.fromString("3f888bd6-e8ff-48ca-8394-fc19dd11d063")});

    }
}
