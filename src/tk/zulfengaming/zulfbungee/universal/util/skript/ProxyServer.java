package tk.zulfengaming.zulfbungee.universal.util.skript;

import tk.zulfengaming.zulfbungee.universal.socket.ClientInfo;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProxyServer server = (ProxyServer) o;
        return Objects.equals(name, server.name) && Objects.equals(clientInfo, server.clientInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, clientInfo);
    }

    public String getName() {
        return name;
    }

    public ClientInfo getServerInfo() {
        return clientInfo;
    }
}
