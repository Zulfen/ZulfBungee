package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.PlayerMessage;

public class PlayerSendMessage<P> extends PacketHandler<P> {

    public PlayerSendMessage(MainServer<P> mainServerIn) {
        super(mainServerIn, PacketTypes.PLAYER_SEND_MESSAGE);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> address) {

        PlayerMessage playerMessage = (PlayerMessage) packetIn.getDataSingle();

        BaseServerConnection<P> connectionFromName = getMainServer().getConnectionFromName(playerMessage.getFromServer().getName());

        if (connectionFromName != null) {
            connectionFromName.sendDirect(packetIn);
        }

        return null;

    }
}
