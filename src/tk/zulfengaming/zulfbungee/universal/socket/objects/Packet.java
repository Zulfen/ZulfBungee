package tk.zulfengaming.zulfbungee.universal.socket.objects;

import java.io.Serializable;

public class Packet implements Serializable {

    private static final long serialVersionUID = 38_573_475_842L;

    private final PacketTypes type;

    private final boolean returnable;

    private final boolean shouldHandle;

    private Object[] data = new Object[1];

    // these arguments should be self-explanatory, but the field shouldHandle could be
    // seen as misleading.

    // that field is used to determine whether it automatically gets handled as soon
    // as the ClientConnection receives it.

    // packets sent by skript should set this field to false

    public Packet(PacketTypes packetType, boolean isReturnable, boolean handleIn, Object[] dataIn) {
        this.type = packetType;
        this.returnable = isReturnable;
        this.shouldHandle = handleIn;
        this.data = dataIn;
    }

    public Packet(PacketTypes packetType, boolean isReturnable, boolean handleIn, Object dataIn) {
        this.type = packetType;
        this.returnable = isReturnable;
        this.shouldHandle = handleIn;
        this.data[0] = dataIn;
    }


    public PacketTypes getType() {
        return type;
    }

    public Object[] getDataArray() {
        return data;
    }

    public Object getDataSingle() {
        return data[0];
    }

    public boolean isReturnable() {
        return returnable;
    }

    public boolean shouldHandle() {
        return shouldHandle;
    }

    @Override
    public String toString() {
        return "Packet{" +
                "type=" + type +
                ", returnable=" + returnable +
                ", shouldHandle=" + shouldHandle +
                ", size=" + data.length +
                '}';
    }
}
