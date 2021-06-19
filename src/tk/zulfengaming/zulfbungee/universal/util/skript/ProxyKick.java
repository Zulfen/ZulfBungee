package tk.zulfengaming.zulfbungee.universal.util.skript;

import java.io.Serializable;

public class ProxyKick implements Serializable {

    private final String[] reason;

    private final ProxyPlayer player;

    public ProxyKick(String[] reason, ProxyPlayer player) {
        this.reason = reason;
        this.player = player;
    }

    public String[] getReason() {
        return reason;
    }

    public ProxyPlayer getPlayer() {
        return player;
    }
}
