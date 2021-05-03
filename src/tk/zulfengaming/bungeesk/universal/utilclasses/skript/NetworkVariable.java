package tk.zulfengaming.bungeesk.universal.utilclasses.skript;

import java.io.Serializable;
import java.util.Optional;

public class NetworkVariable implements Serializable {

    private String changeModeAsString = null;
    private final String name;
    private Value[] values = new Value[1];

    public NetworkVariable(String nameIn, String modeIn, Value[] values) {
        this.name = nameIn;

        this.changeModeAsString = modeIn;
        this.values = values;

    }

    public NetworkVariable(String nameIn, String modeIn, Value value) {
        this.name = nameIn;

        this.values[0] = value;
    }

    public Optional<SkriptChangeMode> getChangeMode() {
        return Optional.ofNullable(SkriptChangeMode.valueOf(changeModeAsString));
    }

    public boolean isDataValid() {
        return values != null && values.length != 0;
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
