package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.MainServer;
import tk.zulfengaming.zulfbungee.bungeecord.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

public class Heartbeat extends PacketHandler {

    public Heartbeat(MainServer mainServerIn) {
        super(mainServerIn, PacketTypes.HEARTBEAT);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection connection) {
        connection.setPing((Long) packetIn.getDataSingle());
        return packetIn;
    }
}