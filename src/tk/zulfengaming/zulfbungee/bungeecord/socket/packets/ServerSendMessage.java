package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.MainServer;
import tk.zulfengaming.zulfbungee.bungeecord.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ServerMessage;

public class ServerSendMessage extends PacketHandler {

    public ServerSendMessage(MainServer mainServerIn) {
        super(mainServerIn, PacketTypes.SERVER_SEND_MESSAGE_EVENT);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection address) {

        ServerMessage message = (ServerMessage) packetIn.getDataSingle();

        for (ProxyServer server : message.getServers()) {

            String serverName = server.getName();

            if (getMainServer().getServerNames().contains(serverName)) {
                getMainServer().getFromName(serverName).send(new Packet(PacketTypes.SERVER_SEND_MESSAGE_EVENT, false, true, message));
            }
        }

        return null;
    }
}
