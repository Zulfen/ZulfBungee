package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import com.google.common.collect.BiMap;
import tk.zulfengaming.zulfbungee.bungeecord.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.ServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.net.SocketAddress;

public class ServerSendMessage extends PacketHandler {

    public ServerSendMessage(Server serverIn) {
        super(serverIn, PacketTypes.SERVER_SEND_MESSAGE_EVENT);

    }

    // not implemented
    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        Object[] data = packetIn.getDataArray();
        String message = (String) data[data.length - 1];

        BiMap<String, ServerConnection> connections = getMainServer().getActiveConnections();

        for (int i = 0; i < data.length - 1; i++) {

            String serverName = (String) data[i];

            if (connections.containsKey(serverName)) {
                connections.get(serverName).send(new Packet(PacketTypes.SERVER_SEND_MESSAGE_EVENT, false, true, message));
            }

        }

        return null;
    }
}
