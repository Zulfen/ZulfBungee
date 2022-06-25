package tk.zulfengaming.zulfbungee.universal.util.skript;

import java.io.Serializable;
import java.util.Arrays;

// From Skungee 2.0.0:
// https://github.com/Skungee/Skungee-2.0.0/blob/master/src/main/java/com/skungee/shared/objects/NetworkVariable.java

public final class Value implements Serializable {

    private static final long serialVersionUID = 1428760897685648784L;

    public final String type;
    public final byte[] data;

    public Value(String type, byte[] data) {
        this.type = type;
        this.data = data;
    }

    public String toString() {
        return "type=" + type + ", data=" + Arrays.toString(data);
    }

    public boolean isSimilar(Value compare) {
        return compare.type.equals(type) && Arrays.equals(compare.data, data);
    }

}
