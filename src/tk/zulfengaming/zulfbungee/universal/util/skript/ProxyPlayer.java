package tk.zulfengaming.zulfbungee.universal.util.skript;

import java.io.Serializable;
import java.util.UUID;

public class ProxyPlayer implements Serializable {

    private static final long serialVersionUID = 45737538534983L;

    private final String name;

    private ProxyServer server = null;

    private final UUID uuid;

    public ProxyPlayer(String nameIn, UUID uuidIn) {
        this.name = nameIn;
        this.uuid = uuidIn;
    }

    public ProxyPlayer(String nameIn, UUID uuidIn, ProxyServer serverIn) {
        this.name = nameIn;
        this.uuid = uuidIn;
        this.server = serverIn;
    }

    public void setServer(ProxyServer server) {
        this.server = server;
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


}
