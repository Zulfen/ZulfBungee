package tk.zulfengaming.zulfbungee.universal.utilclasses.skript;

import java.io.Serializable;

public class ProxyServer implements Serializable {

    private final String name;

    public ProxyServer(String nameIn) {
        this.name = nameIn;
    }

    public String getName() {
        return name;
    }
}
