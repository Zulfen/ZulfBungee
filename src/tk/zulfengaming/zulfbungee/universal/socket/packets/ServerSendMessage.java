package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ServerMessage;

import java.util.Optional;

public class ServerSendMessage<P> extends PacketHandler<P> {

    public ServerSendMessage(PacketHandlerManager<P> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> address) {

        ServerMessage message = (ServerMessage) packetIn.getDataSingle();

        for (ClientServer server : message.getServers()) {

            String serverName = server.getName();

            Optional<BaseServerConnection<P>> connectionFromName = getMainServer().getConnection(serverName);

            connectionFromName.ifPresent(pBaseServerConnection -> pBaseServerConnection
                    .sendDirect(new Packet(PacketTypes.SERVER_SEND_MESSAGE_EVENT, false, true, message)));

        }

        return null;
    }
}
