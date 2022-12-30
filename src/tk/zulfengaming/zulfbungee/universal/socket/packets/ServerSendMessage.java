package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyServer;
import tk.zulfengaming.zulfbungee.universal.skript.ServerMessage;

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
                getMainServer().getConnectionFromName(serverName).sendDirect(new Packet(PacketTypes.SERVER_SEND_MESSAGE_EVENT, false, true, message));
            }
        }

        return null;
    }
}
