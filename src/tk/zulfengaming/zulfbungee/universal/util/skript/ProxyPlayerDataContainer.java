package tk.zulfengaming.zulfbungee.universal.util.skript;

import java.io.Serializable;

public class ProxyPlayerDataContainer implements Serializable {

    private final Object data;

    private ProxyPlayer[] players = new ProxyPlayer[1];

    public ProxyPlayerDataContainer(Object dataIn, ProxyPlayer[] playersIn) {
        this.data = dataIn;
        this.players = playersIn;
    }

    public ProxyPlayerDataContainer(Object dataIn, ProxyPlayer playerIn) {
        this.data = dataIn;
        players[0] = playerIn;
    }

    public Object getData() {
        return data;
    }

    public ProxyPlayer[] getPlayers() {
        return players;
    }

}
