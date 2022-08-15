package tk.zulfengaming.zulfbungee.universal.util.skript;

import tk.zulfengaming.zulfbungee.universal.socket.ServerInfo;

import java.io.Serializable;
import java.util.Objects;

public class ProxyServer implements Serializable {

    private final String name;

    private ServerInfo serverInfo = null;

    public ProxyServer(String nameIn) {
        this.name = nameIn;
    }

    public ProxyServer(String nameIn, ServerInfo infoIn) {
        this.name = nameIn;
        this.serverInfo = infoIn;

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProxyServer server = (ProxyServer) o;
        return Objects.equals(name, server.name) && Objects.equals(serverInfo, server.serverInfo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, serverInfo);
    }

    public String getName() {
        return name;
    }

    public ServerInfo getServerInfo() {
        return serverInfo;
    }
}
