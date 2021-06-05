package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.net.SocketAddress;

public class ServerOnline extends PacketHandler {

    public ServerOnline(Server serverIn) {
        super(serverIn, PacketTypes.SERVER_ONLINE);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        ProxyServer server = (ProxyServer) packetIn.getDataSingle();

        if (getMainServer().getActiveConnections().containsKey(server.getName())) {
            return new Packet(PacketTypes.SERVER_ONLINE, false, false, true);
        }

        return new Packet(PacketTypes.SERVER_ONLINE, false, false, false);
    }
}