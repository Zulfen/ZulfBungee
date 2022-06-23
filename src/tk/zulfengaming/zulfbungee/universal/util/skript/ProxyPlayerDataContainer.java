package tk.zulfengaming.zulfbungee.universal.util.skript;

import java.io.Serializable;

public class ProxyPlayerDataContainer implements Serializable {

    private final Object data;

    private final ProxyPlayer[] players;

    public ProxyPlayerDataContainer(Object dataIn, ProxyPlayer[] playersIn) {
        this.data = dataIn;
        this.players = playersIn;
    }

    public Object getData() {
        return data;
    }

    public ProxyPlayer[] getPlayers() {
        return players;
    }

}
