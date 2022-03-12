package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

public class PlayerServer extends PacketHandler {

    public PlayerServer(Server serverIn) {
        super(serverIn, PacketTypes.PLAYER_SERVER);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection address) {

        ProxyPlayer playerIn = (ProxyPlayer) packetIn.getDataSingle();
        ProxyServer serverOut = null;

        if (getProxy().getPlayer(playerIn.getUuid()).getServer() != null)
            serverOut = new ProxyServer(getProxy().getPlayer(playerIn.getUuid()).getServer().getInfo().getName());

        return new Packet(PacketTypes.PLAYER_SERVER, false, false, serverOut);
    }
}