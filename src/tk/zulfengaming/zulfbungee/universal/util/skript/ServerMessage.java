package tk.zulfengaming.zulfbungee.universal.util.skript;

import java.io.Serializable;

public class ServerMessage implements Serializable {

    private final String message;

    private final String title;

    private final ProxyServer[] servers;

    public ServerMessage(String titleIn, String messageIn, ProxyServer[] serversIn) {
        this.title = titleIn;
        this.message = messageIn;
        this.servers = serversIn;
    }

    public String getTitle() {
        return title;
    }

    public String getText() {
        return message;
    }

    public ProxyServer[] getServers() {
        return servers;
    }
}
