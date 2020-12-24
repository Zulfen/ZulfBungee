package tk.zulfengaming.bungeesk.bungeecord.socket.packets;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.json.simple.JSONObject;
import tk.zulfengaming.bungeesk.bungeecord.socket.PacketHandler;
import tk.zulfengaming.bungeesk.bungeecord.socket.Server;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.net.SocketAddress;
import java.util.Collection;

public class GlobalPlayers extends PacketHandler {

    Server server;

    public GlobalPlayers(Server serverIn) {
        super(PacketTypes.GLOBAL_PLAYERS);

        this.server = serverIn;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Packet handlePacket(Packet packetIn, SocketAddress address) {
        JSONObject packetData = new JSONObject();
        final Collection<ProxiedPlayer> players = server.instance.getProxy().getPlayers();

        packetData.put("value", players.size());

        return new Packet(null, null, PacketTypes.GLOBAL_PLAYERS, packetData, false);
    }
}
