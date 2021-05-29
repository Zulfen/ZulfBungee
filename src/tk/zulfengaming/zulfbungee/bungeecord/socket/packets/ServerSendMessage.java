package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import com.google.common.collect.BiMap;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.ServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ServerMessage;

import java.net.SocketAddress;

public class ServerSendMessage extends PacketHandler {

    public ServerSendMessage(Server serverIn) {
        super(serverIn, PacketTypes.SERVER_SEND_MESSAGE_EVENT);

    }

    // not implemented
    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        ServerMessage message = (ServerMessage) packetIn.getDataSingle();

        BiMap<String, ServerConnection> connections = getMainServer().getActiveConnections();

        for (ProxyServer server : message.getServers()) {

            String serverName = server.getName();

            if (connections.containsKey(serverName)) {
                connections.get(serverName).send(new Packet(PacketTypes.SERVER_SEND_MESSAGE_EVENT, false, true, message));
            }
        }

        return null;
    }
}
