package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

public class ProxyPlayerOnline<P> extends PacketHandler<P> {

    public ProxyPlayerOnline(MainServer<P> mainServerIn) {
        super(mainServerIn, PacketTypes.PLAYER_ONLINE);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> address) {

        ClientPlayer skriptPlayer = (ClientPlayer) packetIn.getDataSingle();

        if (skriptPlayer != null) {

            ZulfProxyPlayer<P> proxiedPlayer = getProxy().getPlayer(skriptPlayer.getUuid());

            if (proxiedPlayer != null) {
                return new Packet(PacketTypes.PLAYER_ONLINE, false, false, true);
            }

        }

        return new Packet(PacketTypes.PLAYER_ONLINE, false, false, false);
    }
}