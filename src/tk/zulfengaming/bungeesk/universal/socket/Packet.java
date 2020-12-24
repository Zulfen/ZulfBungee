package tk.zulfengaming.bungeesk.universal.socket;

import org.json.simple.JSONObject;

import java.io.Serializable;
import java.net.SocketAddress;

public class Packet implements Serializable {

    public SocketAddress address;
    public String name;

    public PacketTypes type;

    public boolean returnable;
    public JSONObject data;

    public Packet(SocketAddress serverAddress, String serverName, PacketTypes packetType, JSONObject packetData, boolean isReturnable) {
        this.address = serverAddress;
        this.name = serverName;

        this.type = packetType;
        this.returnable = isReturnable;
        this.data = packetData;
    }

}
