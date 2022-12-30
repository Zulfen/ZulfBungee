package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyPlayer;

// used when you only have the player's name but not the uuid
public class ProxyPlayerUUID extends PacketHandler {

    public ProxyPlayerUUID(MainServer mainServerIn) {
        super(mainServerIn, PacketTypes.PROXY_PLAYER_UUID);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection connectionIn) {

        String playerName = (String) packetIn.getDataSingle();
        ProxyPlayer proxiedPlayer = getProxy().getPlayer(playerName);

        if (proxiedPlayer != null) {
            return new Packet(PacketTypes.PROXY_PLAYER_UUID, false, false, proxiedPlayer.getUuid());
        }

        return new Packet(PacketTypes.PROXY_PLAYER_UUID, false, false, new Object[0]);

    }
}