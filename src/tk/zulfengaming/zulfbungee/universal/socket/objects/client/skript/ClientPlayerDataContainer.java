package tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript;

import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;

import java.io.Serializable;

public class ClientPlayerDataContainer implements Serializable {

    private final Object[] data = new Object[1];

    private ClientPlayer[] players = new ClientPlayer[1];

    public ClientPlayerDataContainer(Object dataIn, ClientPlayer[] playersIn) {
        this.data[0] = dataIn;
        this.players = playersIn;
    }

    public ClientPlayerDataContainer(Object dataIn, ClientPlayer playerIn) {
        this.data[0] = dataIn;
        this.players[0] = playerIn;
    }

    public Object getDataSingle() {
        return data[0];
    }

    public Object[] getDataArray() {
        return data;
    }

    public ClientPlayer[] getPlayers() {
        return players;
    }

}
