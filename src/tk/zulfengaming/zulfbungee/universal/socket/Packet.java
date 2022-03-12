package tk.zulfengaming.zulfbungee.universal.socket;

import java.io.Serializable;
import java.util.UUID;

public class Packet implements Serializable {

    private static final long serialVersionUID = 38_573_475_842L;

    private final PacketTypes type;

    private final boolean returnable;

    private final boolean shouldHandle;

    private final UUID id;

    private Object[] data = new Object[1];

    // these arguments should be self-explanatory, but the field shouldHandle could be
    // seen as misleading.

    // that field is used to determine whether it automatically gets handled as soon
    // as the ClientConnection receives it. an example of a packet that uses this
    // is the heartbeat packet, as it is separate from Skript.

    // packets sent by skript should set this field to false

    public Packet(PacketTypes packetType, boolean isReturnable, boolean handleIn, Object[] dataIn) {

        this.type = packetType;
        this.returnable = isReturnable;
        this.shouldHandle = handleIn;
        this.id = UUID.randomUUID();

        this.data = dataIn;
    }

    public Packet(PacketTypes packetType, boolean isReturnable, boolean handleIn, Object dataIn) {
        this.type = packetType;
        this.returnable = isReturnable;
        this.shouldHandle = handleIn;
        this.id = UUID.randomUUID();

        this.data[0] = dataIn;
    }

    public Packet(PacketTypes packetType, boolean isReturnable, boolean handleIn, Object[] dataIn, UUID uuidIn) {

        this.type = packetType;
        this.returnable = isReturnable;
        this.shouldHandle = handleIn;
        this.id = uuidIn;

        this.data = dataIn;
    }

    public Packet(PacketTypes packetType, boolean isReturnable, boolean handleIn, Object dataIn, UUID uuidIn) {
        this.type = packetType;
        this.returnable = isReturnable;
        this.shouldHandle = handleIn;
        this.id = uuidIn;

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

    public UUID getId() {
        return id;
    }

}
