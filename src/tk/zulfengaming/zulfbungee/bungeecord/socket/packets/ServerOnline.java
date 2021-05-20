package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.net.SocketAddress;
import java.util.Collection;

public class ServerOnline extends PacketHandler {

    public ServerOnline(Server serverIn) {
        super(serverIn, PacketTypes.SERVER_ONLINE);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        String serverName = (String) packetIn.getDataSingle();

        Collection<String> activeConnections = getMainServer().getActiveConnections().keySet();

        if (activeConnections.contains(serverName)) {
            return new Packet(PacketTypes.SERVER_ONLINE, false, false, true);
        }

        return new Packet(PacketTypes.SERVER_ONLINE, false, false, false);
    }
}