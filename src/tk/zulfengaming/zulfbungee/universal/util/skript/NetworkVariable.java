package tk.zulfengaming.zulfbungee.universal.util.skript;

import java.io.Serializable;
import java.util.Optional;

public class NetworkVariable implements Serializable {

    private String changeModeAsString = "";
    private final String name;
    private Value[] values = new Value[1];

    public NetworkVariable(String nameIn, String modeIn, Value[] values) {
        this.name = nameIn;
        this.changeModeAsString = modeIn;
        this.values = values;

    }

    public NetworkVariable(String nameIn, Value value) {
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
