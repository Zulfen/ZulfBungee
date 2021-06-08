package tk.zulfengaming.zulfbungee.universal.util.skript;

import java.io.Serializable;

public class ServerMessage implements Serializable {

    private final String message;

    private final String title;

    private final ProxyServer[] servers;

    private final ProxyServer from;

    public ServerMessage(String titleIn, String messageIn, ProxyServer[] serversIn, ProxyServer fromIn) {
        this.title = titleIn;
        this.message = messageIn;
        this.servers = serversIn;
        this.from = fromIn;
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

    public ProxyServer getFrom() {
        return from;
    }

}
