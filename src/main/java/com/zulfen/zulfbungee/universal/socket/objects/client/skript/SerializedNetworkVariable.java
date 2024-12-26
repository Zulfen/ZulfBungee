package com.zulfen.zulfbungee.universal.socket.objects.client.skript;

import java.io.Serializable;
import java.util.Optional;

// list will have ::* as the name when returned
public class SerializedNetworkVariable implements Serializable {

    private String changeModeAsString = "";
    private final String name;
    private Value[] values = new Value[1];

    public SerializedNetworkVariable(String nameIn, String modeIn, Value[] values) {
        this.name = nameIn;
        this.changeModeAsString = modeIn;
        this.values = values;
    }

    public SerializedNetworkVariable(String nameIn, Value value) {
        this.name = nameIn;
        this.values[0] = value;
    }

    public Optional<SkriptChangeMode> getChangeMode() {
        return !changeModeAsString.isEmpty() ? Optional.of(SkriptChangeMode.valueOf(changeModeAsString)) : Optional.empty();
    }

    public String getName() {
        return name;
    }

    public Value[] getValueArray() {
        return values;
    }

    public Value getSingleValue() {
        return values[0];
    }
}
