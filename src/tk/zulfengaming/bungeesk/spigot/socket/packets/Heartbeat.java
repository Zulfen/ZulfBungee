package tk.zulfengaming.bungeesk.spigot.socket.packets;

import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;
import tk.zulfengaming.bungeesk.spigot.handlers.PacketHandler;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.net.SocketAddress;

public class Heartbeat extends PacketHandler {

    public Heartbeat(ClientConnection connection) {
        super(connection, true, PacketTypes.HEARTBEAT);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {
        return packetIn;

    }
}
