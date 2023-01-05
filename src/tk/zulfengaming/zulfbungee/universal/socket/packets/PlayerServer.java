package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;

public class PlayerServer<P> extends PacketHandler<P> {

    public PlayerServer(MainServer<P> mainServerIn) {
        super(mainServerIn, PacketTypes.PLAYER_SERVER);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection) {

        ClientPlayer playerIn = (ClientPlayer) packetIn.getDataSingle();

        ZulfProxyServer<P> server = getProxy().getPlayer(playerIn.getUuid()).getServer();

        if (server != null) {

            String name = server.getName();

            return new Packet(PacketTypes.PLAYER_SERVER, false, false, name);

        } else {
            return new Packet(PacketTypes.PLAYER_SERVER, false, false, new Object[0]);
        }

    }
}