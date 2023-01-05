package tk.zulfengaming.zulfbungee.spigot.interfaces;

import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;

import java.net.SocketAddress;

public abstract class PacketHandler {

    private final PacketTypes[] types;

    private final ClientConnection connection;

    public abstract void handlePacket(Packet packetIn, SocketAddress address);

    public PacketTypes[] getTypes() {
        return types;
    }

    public ClientConnection getConnection() {
        return connection;
    }

    public PacketHandler(ClientConnection connectionIn, PacketTypes... types){

        this.connection = connectionIn;
        this.types = types;

    }

}
