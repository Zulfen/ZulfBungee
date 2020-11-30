package tk.zulfengaming.bungeesk.universal.socket;

import org.json.simple.JSONObject;

import java.io.Serializable;
import java.net.InetAddress;

public class Packet implements Serializable {

    public InetAddress address;
    public String name;

    public PacketTypes type;

    public boolean returnable;
    public JSONObject data;

    public Packet(InetAddress serverAddress, String serverName, PacketTypes packetType, JSONObject packetData, boolean isReturnable) {
        this.address = serverAddress;
        this.name = serverName;

        this.type = packetType;
        this.returnable = isReturnable;
        this.data = packetData;
    }

}
