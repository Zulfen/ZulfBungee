package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;

import java.net.SocketAddress;

public class ProxyPlayerOnline extends PacketHandler {

    public ProxyPlayerOnline(Server serverIn) {
        super(serverIn, PacketTypes.PLAYER_ONLINE);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        ProxyPlayer skriptPlayer = (ProxyPlayer) packetIn.getDataSingle();

        if (skriptPlayer != null) {

            ProxiedPlayer proxiedPlayer = getProxy().getPlayer(skriptPlayer.getUuid());

            if (proxiedPlayer != null) {
                return new Packet(PacketTypes.PLAYER_ONLINE, false, false, true);
            }

        }

        return new Packet(PacketTypes.PLAYER_ONLINE, false, false, false);
    }
}