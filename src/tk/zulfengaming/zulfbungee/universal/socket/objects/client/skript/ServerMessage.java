package tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript;

import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;

import java.io.Serializable;

public class ServerMessage implements Serializable {

    private final String message;

    private final String title;

    private final ClientServer[] servers;

    private final ClientServer from;

    public ServerMessage(String titleIn, String messageIn, ClientServer[] serversIn, ClientServer fromIn) {
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

    public ClientServer[] getServers() {
        return servers;
    }

    public ClientServer getFrom() {
        return from;
    }

}
