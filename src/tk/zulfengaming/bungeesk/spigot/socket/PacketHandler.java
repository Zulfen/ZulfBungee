package tk.zulfengaming.bungeesk.spigot.socket;

import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.net.SocketAddress;

public abstract class PacketHandler {

    private PacketTypes[] types;

    public abstract Object handlePacket(Packet packetIn, SocketAddress address);

    public PacketTypes[] getTypes() {
        return types;
    }

    public PacketHandler(PacketTypes... types){
        this.types = types;

    }

}
