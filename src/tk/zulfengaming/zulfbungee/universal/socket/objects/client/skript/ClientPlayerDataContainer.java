package tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript;

import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;

import java.io.Serializable;

public class ClientPlayerDataContainer implements Serializable {

    private final Object data;

    private ClientPlayer[] players = new ClientPlayer[1];

    public ClientPlayerDataContainer(Object dataIn, ClientPlayer[] playersIn) {
        this.data = dataIn;
        this.players = playersIn;
    }

    public ClientPlayerDataContainer(Object dataIn, ClientPlayer playerIn) {
        this.data = dataIn;
        players[0] = playerIn;
    }

    public Object getData() {
        return data;
    }

    public ClientPlayer[] getPlayers() {
        return players;
    }

}
