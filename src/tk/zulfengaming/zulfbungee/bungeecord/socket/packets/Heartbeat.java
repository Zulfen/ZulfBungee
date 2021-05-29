package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.net.SocketAddress;

public class Heartbeat extends PacketHandler {

    public Heartbeat(Server serverIn) {
        super(serverIn, PacketTypes.HEARTBEAT);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {
        return packetIn;
    }
}