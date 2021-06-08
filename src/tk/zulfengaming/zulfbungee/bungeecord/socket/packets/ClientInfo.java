package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.ServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.net.SocketAddress;

public class ClientInfo extends PacketHandler {

    public ClientInfo(Server serverIn) {
        super(serverIn, PacketTypes.CLIENT_INFO);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        ProxyServer serverIn = (ProxyServer) packetIn.getDataSingle();

        ServerConnection connection = getMainServer().getActiveConnections().get(serverIn.getName());

        return new Packet(PacketTypes.CLIENT_INFO, false, false, connection.getClientInfo());

    }
}