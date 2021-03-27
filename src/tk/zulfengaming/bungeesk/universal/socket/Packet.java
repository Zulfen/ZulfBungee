package tk.zulfengaming.bungeesk.universal.socket;

import java.io.Serializable;
import java.util.UUID;

public class Packet implements Serializable {

    private static final long serialVersionUID = 45_737_538_534_983L;

    private final String name;

    private final PacketTypes type;

    private final boolean returnable;

    private final boolean shouldHandle;

    private final UUID id;

    private final Object[] data;

    // these arguments should be self explanatory, but the field shouldHandle could be
    // seen as misleading.

    // that field is used to determine whether it automatically gets handled as soon
    // as the ClientConnection receives it. an example of a packet that uses this
    // is the heartbeat packet, as it is separate from Skript.

    // packets sent by skript should set this field to false

    public Packet(String serverName, PacketTypes packetType, boolean isReturnable, boolean handleIn, Object[] dataIn) {
        this.name = serverName;

        this.type = packetType;
        this.returnable = isReturnable;
        this.shouldHandle = handleIn;
        this.id = UUID.randomUUID();

        this.data = dataIn;
    }

    public String getName() {
        return name;
    }

    public PacketTypes getType() {
        return type;
    }

    public Object[] getData() {
        return data;
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
