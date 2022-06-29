package tk.zulfengaming.zulfbungee.universal.socket;

import java.io.Serializable;

public class ClientInfo implements Serializable {

    private final int maxPlayers;

    private final int minecraftPort;

    public ClientInfo(int maxPlayersIn, int minecraftPortIn) {
        this.maxPlayers = maxPlayersIn;
        this.minecraftPort = minecraftPortIn;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinecraftPort() {
        return minecraftPort;
    }
}
