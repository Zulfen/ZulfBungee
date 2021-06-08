package tk.zulfengaming.zulfbungee.universal.util.skript;

import java.io.Serializable;

public class ProxyServer implements Serializable {

    private final String name;

    private ClientInfo clientInfo = null;

    public ProxyServer(String nameIn) {
        this.name = nameIn;
    }

    public ProxyServer(String nameIn, ClientInfo infoIn) {
        this.name = nameIn;
        this.clientInfo = infoIn;

    }

    public String getName() {
        return name;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }
}
