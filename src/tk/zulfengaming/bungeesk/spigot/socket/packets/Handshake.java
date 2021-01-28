package tk.zulfengaming.bungeesk.spigot.socket.packets;

import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;
import tk.zulfengaming.bungeesk.spigot.socket.PacketHandler;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.net.SocketAddress;

public class Handshake extends PacketHandler {

    public Handshake(ClientConnection connection) {
        super(connection, PacketTypes.HANDSHAKE);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {
        getConnection().instance.log("Packet recieved !!!");
        return packetIn;

    }
}
