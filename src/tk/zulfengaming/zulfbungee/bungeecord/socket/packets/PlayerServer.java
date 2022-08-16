package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.MainServer;
import tk.zulfengaming.zulfbungee.bungeecord.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;

public class PlayerServer extends PacketHandler {

    public PlayerServer(MainServer mainServerIn) {
        super(mainServerIn, PacketTypes.PLAYER_SERVER);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection connection) {

        ProxyPlayer playerIn = (ProxyPlayer) packetIn.getDataSingle();

        net.md_5.bungee.api.connection.Server server = getProxy().getPlayer(playerIn.getUuid()).getServer();

        if (server != null) {

            String name = server.getInfo().getName();

            return new Packet(PacketTypes.PLAYER_SERVER, false, false, name);

        } else {
            return new Packet(PacketTypes.PLAYER_SERVER, false, false, new Object[0]);
        }

    }
}