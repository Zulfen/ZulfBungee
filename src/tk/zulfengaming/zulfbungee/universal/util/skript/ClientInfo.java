package tk.zulfengaming.zulfbungee.universal.util.skript;

import java.io.Serializable;

public class ClientInfo implements Serializable {

    private final int maxPlayers;

    public ClientInfo(int maxPlayersIn) {
        this.maxPlayers = maxPlayersIn;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }
}
