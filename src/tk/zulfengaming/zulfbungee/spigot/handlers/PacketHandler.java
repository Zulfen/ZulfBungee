package tk.zulfengaming.zulfbungee.spigot.handlers;

import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.net.SocketAddress;

public abstract class PacketHandler {

    private final PacketTypes[] types;

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
