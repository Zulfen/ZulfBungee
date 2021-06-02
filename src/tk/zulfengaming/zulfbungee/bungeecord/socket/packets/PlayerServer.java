package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.net.SocketAddress;
import java.util.ArrayList;

public class PlayerServer extends PacketHandler {

    public PlayerServer(Server serverIn) {
        super(serverIn, PacketTypes.PLAYER_SERVER);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        ProxyPlayer playerIn = (ProxyPlayer) packetIn.getDataSingle();
        ProxyServer serverOut = null;

        for (ProxyServer server : getMainServer().getServers().values()) {
            ArrayList<ProxyPlayer> players = server.getPlayers();

            for (ProxyPlayer player : players) {
                if (player.equals(playerIn)) {
                    serverOut = server;
                    break;
                }
            }
        }

        return new Packet(PacketTypes.PLAYER_SERVER, false, false, serverOut);
    }
}