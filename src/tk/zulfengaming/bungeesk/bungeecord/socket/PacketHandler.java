package tk.zulfengaming.bungeesk.bungeecord.socket;

import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.net.SocketAddress;

public abstract class PacketHandler {

    private PacketTypes[] types;

    private final Server socketServer;

    public abstract Packet handlePacket(Packet packetIn, SocketAddress address);

    public PacketTypes[] getTypes() {
        return types;
    }

    public Server getSocketServer() {
        return socketServer;
    }

    public PacketHandler(Server serverIn, PacketTypes... types) {
        this.socketServer = serverIn;
        this.types = types;

    }

}
