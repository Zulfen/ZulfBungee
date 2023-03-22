package tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript;

import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;

import java.io.Serializable;

public class ClientServerDataContainer implements Serializable {

    private final ClientServer[] servers;
    private final Object data;

    public ClientServerDataContainer(ClientServer[] serversIn, Object dataIn) {
        this.servers = serversIn;
        this.data = dataIn;
    }

    public ClientServer[] getServers() {
        return servers;
    }

    public Object getData() {
        return data;
    }

}
