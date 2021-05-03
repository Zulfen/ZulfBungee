package tk.zulfengaming.bungeesk.universal.utilclasses.skript;

import java.io.Serializable;
import java.util.Arrays;

public final class Value implements Serializable {

    private static final long serialVersionUID = 1428760897685648784L;

    public String type;
    public byte[] data;

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
