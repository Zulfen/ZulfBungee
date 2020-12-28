package tk.zulfengaming.bungeesk.bungeecord.socket.packets;

import tk.zulfengaming.bungeesk.bungeecord.socket.PacketHandler;
import tk.zulfengaming.bungeesk.bungeecord.socket.Server;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.net.SocketAddress;

public class Handshake extends PacketHandler {

    public Handshake(Server serverIn) {
        super(serverIn, PacketTypes.HANDSHAKE);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {
        return packetIn;

    }
}
