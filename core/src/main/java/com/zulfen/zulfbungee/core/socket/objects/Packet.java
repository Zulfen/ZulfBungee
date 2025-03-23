package com.zulfen.zulfbungee.core.socket.objects;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class Packet implements Serializable {

    @Serial
    private static final long serialVersionUID = 38_573_475_842L;

    private final PacketTypes type;
    private final UUID id;

    private final boolean returnable;

    private final boolean shouldHandle;

    protected Object[] data = new Object[1];

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
        this.id = UUID.randomUUID();
    }

    public Packet(PacketTypes packetType, boolean isReturnable, boolean handleIn, Object[] dataIn, UUID id) {
        this.type = packetType;
        this.returnable = isReturnable;
        this.shouldHandle = handleIn;
        this.data = dataIn;
        this.id = id;
    }

    public Packet(PacketTypes packetType, boolean isReturnable, boolean handleIn, Object dataIn, UUID id) {
        this.type = packetType;
        this.returnable = isReturnable;
        this.shouldHandle = handleIn;
        this.data[0] = dataIn;
        this.id = id;
    }

    public Packet(PacketTypes packetType, boolean isReturnable, boolean handleIn, Object dataIn) {
        this.type = packetType;
        this.returnable = isReturnable;
        this.shouldHandle = handleIn;
        this.data[0] = dataIn;
        this.id = UUID.randomUUID();
    }

    public Packet(PacketTypes packetType, boolean isReturnable, boolean handleIn) {
        this.type = packetType;
        this.returnable = isReturnable;
        this.shouldHandle = handleIn;
        this.id = UUID.randomUUID();
    }

    public Packet(PacketTypes packetType, boolean isReturnable, boolean handleIn, UUID id) {
        this.type = packetType;
        this.returnable = isReturnable;
        this.shouldHandle = handleIn;
        this.id = id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Packet packet = (Packet) o;
        return returnable == packet.returnable && shouldHandle == packet.shouldHandle && type == packet.type;
    }

    public Packet response(boolean returnable, boolean handleIn, Object[] dataIn) {
        return new Packet(type, returnable, handleIn, dataIn, id);
    }

    public Packet response(boolean returnable, boolean handleIn, Object dataIn) {
        return new Packet(type, returnable, handleIn, dataIn, id);
    }

    public Packet response(boolean returnable, boolean handleIn) {
        return new Packet(type, returnable, handleIn, id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, returnable, shouldHandle);
    }

    @Override
    public String toString() {
        return "Packet{" +
                "type=" + type +
                ", returnable=" + returnable +
                ", shouldHandle=" + shouldHandle +
                ", size=" + data.length +
                ", id=" + id +
                '}';
    }

}
