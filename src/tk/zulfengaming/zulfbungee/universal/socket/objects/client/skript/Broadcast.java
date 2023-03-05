package tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript;

import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;

import java.io.Serializable;

public class Broadcast implements Serializable {

    private final ClientServer[] servers;
    private final String message;

    public Broadcast(ClientServer[] servers, String message) {
        this.servers = servers;
        this.message = message;
    }

    public ClientServer[] getServers() {
        return servers;
    }

    public String getMessage() {
        return message;
    }

}
