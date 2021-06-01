package tk.zulfengaming.zulfbungee.universal.util.skript;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

public class ProxyPlayer implements Serializable {

    private static final long serialVersionUID = 45737538534983L;

    private final String name;

    private final ProxyServer server;

    private final UUID uuid;

    public ProxyPlayer(String nameIn, UUID uuidIn) {
        this.name = nameIn;
        this.uuid = uuidIn;
        this.server = null;
    }

    public ProxyPlayer(String nameIn, UUID uuidIn, ProxyServer serverIn) {
        this.name = nameIn;
        this.uuid = uuidIn;
        this.server = serverIn;
    }

    public ProxyServer getServer() {
        return server;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProxyPlayer that = (ProxyPlayer) o;
        return Objects.equals(name, that.name) && Objects.equals(uuid, that.uuid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, uuid);
    }
}
