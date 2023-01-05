package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ServerMessage;

public class ServerSendMessage<P> extends PacketHandler<P> {

    public ServerSendMessage(MainServer<P> mainServerIn) {
        super(mainServerIn, PacketTypes.SERVER_SEND_MESSAGE_EVENT);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> address) {

        ServerMessage message = (ServerMessage) packetIn.getDataSingle();

        for (ClientServer server : message.getServers()) {

            String serverName = server.getName();

            if (getMainServer().getServerNames().contains(serverName)) {
                getMainServer().getConnectionFromName(serverName)
                        .sendDirect(new Packet(PacketTypes.SERVER_SEND_MESSAGE_EVENT, false, true, message));
            }
        }

        return null;
    }
}
