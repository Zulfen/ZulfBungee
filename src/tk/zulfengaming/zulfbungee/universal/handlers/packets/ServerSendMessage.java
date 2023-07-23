package tk.zulfengaming.zulfbungee.universal.handlers.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.ProxyServerConnection;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ServerMessage;

import java.util.Optional;

public class ServerSendMessage<P, T> extends PacketHandler<P, T> {

    public ServerSendMessage(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> address) {

        ServerMessage message = (ServerMessage) packetIn.getDataSingle();

        for (ClientServer server : message.getServers()) {

            String serverName = server.getName();

            Optional<ProxyServerConnection<P, T>> connectionFromName = getMainServer().getConnection(serverName);

            connectionFromName.ifPresent(pBaseServerConnection -> pBaseServerConnection
                    .sendDirect(new Packet(PacketTypes.SERVER_SEND_MESSAGE_EVENT, false, true, message)));

        }

        return null;
    }
}
