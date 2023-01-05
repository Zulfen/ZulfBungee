package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;

public class Heartbeat<P> extends PacketHandler<P> {

    public Heartbeat(MainServer<P> mainServerIn) {
        super(mainServerIn, PacketTypes.HEARTBEAT);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection) {
        connection.setPing((Long) packetIn.getDataSingle());
        return packetIn;
    }
}