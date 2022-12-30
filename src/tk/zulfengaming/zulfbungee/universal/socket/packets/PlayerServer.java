package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyServer;

public class PlayerServer extends PacketHandler {

    public PlayerServer(MainServer mainServerIn) {
        super(mainServerIn, PacketTypes.PLAYER_SERVER);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection connection) {

        ProxyPlayer playerIn = (ProxyPlayer) packetIn.getDataSingle();

        ProxyServer server = getProxy().getPlayer(playerIn.getUuid()).getServer();

        if (server != null) {

            String name = server.getName();

            return new Packet(PacketTypes.PLAYER_SERVER, false, false, name);

        } else {
            return new Packet(PacketTypes.PLAYER_SERVER, false, false, new Object[0]);
        }

    }
}