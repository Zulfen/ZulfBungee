package tk.zulfengaming.bungeesk.bungeecord.socket.packets;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.zulfengaming.bungeesk.bungeecord.handlers.PacketHandler;
import tk.zulfengaming.bungeesk.bungeecord.socket.Server;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.net.SocketAddress;
import java.util.Collection;

public class GlobalPlayers extends PacketHandler {

    public GlobalPlayers(Server serverIn) {
        super(serverIn, PacketTypes.GLOBAL_PLAYERS);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        final Collection<ProxiedPlayer> players = getSocketServer().getPluginInstance().getProxy().getPlayers();

        JsonObject data = new JsonObject();
        data.


        return packetOut;
    }
}
