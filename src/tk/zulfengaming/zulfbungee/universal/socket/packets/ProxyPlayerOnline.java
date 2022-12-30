package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyPlayer;

public class ProxyPlayerOnline extends PacketHandler {

    public ProxyPlayerOnline(MainServer mainServerIn) {
        super(mainServerIn, PacketTypes.PLAYER_ONLINE);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection address) {

        ProxyPlayer skriptPlayer = (ProxyPlayer) packetIn.getDataSingle();

        if (skriptPlayer != null) {

            ProxyPlayer proxiedPlayer = getProxy().getPlayer(skriptPlayer.getUuid());

            if (proxiedPlayer != null) {
                return new Packet(PacketTypes.PLAYER_ONLINE, false, false, true);
            }

        }

        return new Packet(PacketTypes.PLAYER_ONLINE, false, false, false);
    }
}