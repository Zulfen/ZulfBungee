package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.PlayerMessage;

import java.util.Optional;

public class PlayerSendMessage<P> extends PacketHandler<P> {

    public PlayerSendMessage(PacketHandlerManager<P> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> address) {

        PlayerMessage playerMessage = (PlayerMessage) packetIn.getDataSingle();

        Optional<BaseServerConnection<P>> connectionFromName = getMainServer().getConnection(playerMessage.getFromServer());
        connectionFromName.ifPresent(pBaseServerConnection -> pBaseServerConnection.sendDirect(packetIn));

        return null;

    }
}
