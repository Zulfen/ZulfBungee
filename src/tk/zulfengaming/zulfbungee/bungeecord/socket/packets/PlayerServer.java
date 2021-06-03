package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.ServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerServer extends PacketHandler {

    public PlayerServer(Server serverIn) {
        super(serverIn, PacketTypes.PLAYER_SERVER);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        ProxyPlayer playerIn = (ProxyPlayer) packetIn.getDataSingle();
        ProxyServer serverOut = null;

        for (Map.Entry<String, ServerConnection> entry : getMainServer().getActiveConnections().entrySet()) {

            HashMap<UUID, ProxyPlayer> players = entry.getValue().getPlayers();

            if (players.containsValue(playerIn)) {
                serverOut = new ProxyServer(entry.getKey(), players.values().toArray(new ProxyPlayer[0]));
            }
        }

        return new Packet(PacketTypes.PLAYER_SERVER, false, false, serverOut);
    }
}