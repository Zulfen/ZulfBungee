package tk.zulfengaming.zulfbungee.spigot.interfaces;

import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;

import java.net.SocketAddress;

public abstract class PacketHandler {

    private final PacketTypes[] types;

    private final Connection connection;

    private final boolean isAsync;

    public abstract void handlePacket(Packet packetIn, SocketAddress address);

    public PacketTypes[] getTypes() {
        return types;
    }

    public Connection getConnection() {
        return connection;
    }

    public PacketHandler(Connection connectionIn, boolean isAsync, PacketTypes... types){
        this.connection = connectionIn;
        this.types = types;
        this.isAsync = isAsync;
    }

    public boolean isAsync() {
        return isAsync;
    }

}
