package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.bungeecord.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

// used when you only have the player's name but not the uuid
public class ProxyPlayerUUID extends PacketHandler {

    public ProxyPlayerUUID(MainServer mainServerIn) {
        super(mainServerIn, PacketTypes.PROXY_PLAYER_UUID);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection connectionIn) {

        String playerName = (String) packetIn.getDataSingle();
        ProxiedPlayer proxiedPlayer = getProxy().getPlayer(playerName);

        if (proxiedPlayer != null) {
            return new Packet(PacketTypes.PROXY_PLAYER_UUID, false, false, proxiedPlayer.getUniqueId());
        }

        return new Packet(PacketTypes.PROXY_PLAYER_UUID, false, false, new Object[0]);

    }
}