package tk.zulfengaming.bungeesk.spigot.interfaces;

import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.net.SocketAddress;

public abstract class PacketHandler {

    private PacketTypes[] types;

    private final ClientConnection connection;

    public abstract Packet handlePacket(Packet packetIn, SocketAddress address);

    public PacketTypes[] getTypes() {
        return types;
    }

    public ClientConnection getConnection() {
        return connection;
    }

    public PacketHandler(ClientConnection connection, PacketTypes... types){
        this.connection = connection;
        this.types = types;

    }

}
