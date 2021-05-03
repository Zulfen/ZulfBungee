package tk.zulfengaming.bungeesk.universal.utilclasses.skript;

import java.io.Serializable;
import java.util.UUID;

public class ProxyPlayer implements Serializable {

    private static final long serialVersionUID = 45737538534983L;

    private final String name;

    private final UUID uuid;

    public ProxyPlayer(String nameIn, UUID uuidIn) {
        this.name = nameIn;
        this.uuid = uuidIn;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }


}
