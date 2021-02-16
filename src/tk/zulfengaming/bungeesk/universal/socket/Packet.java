package tk.zulfengaming.bungeesk.universal.socket;

import java.io.Serializable;
import java.net.SocketAddress;

public class Packet implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private final SocketAddress address;
    private final String name;

    private final PacketTypes type;

    private final boolean returnable;
    private final Object data;

    public Packet(SocketAddress serverAddress, String serverName, PacketTypes packetType, boolean isReturnable, Object dataIn) {
        this.address = serverAddress;
        this.name = serverName;

        this.type = packetType;
        this.returnable = isReturnable;

        this.data = dataIn;
    }

    public String getName() {
        return name;
    }

    public PacketTypes getType() {
        return type;
    }

    public Object getData() {
        return data;
    }

    public boolean isReturnable() {
        return returnable;
    }

    public SocketAddress getAddress() {
        return address;
    }

}
