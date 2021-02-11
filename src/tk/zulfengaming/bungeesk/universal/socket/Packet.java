package tk.zulfengaming.bungeesk.universal.socket;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.net.SocketAddress;

public class Packet implements Serializable {

    private static final long serialVersionUID = 7526472295622776147L;

    private final SocketAddress address;
    private final String name;

    private final PacketTypes type;

    private final boolean returnable;
    private JSONObject data;

    public Packet(SocketAddress serverAddress, String serverName, PacketTypes packetType, boolean isReturnable, JSONArray packetData) {
        this.address = serverAddress;
        this.name = serverName;

        this.type = packetType;
        this.returnable = isReturnable;
    }

    public String getName() {
        return name;
    }

    public PacketTypes getType() {
        return type;
    }

    public boolean isReturnable() {
        return returnable;
    }

    public SocketAddress getAddress() {
        return address;
    }

}
