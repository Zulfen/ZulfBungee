package com.zulfen.zulfbungee.universal.socket.objects.client;

import java.io.Serializable;

public class ClientInfo implements Serializable {

    private final int maxPlayers;

    private final int minecraftPort;
    private final String versionString;

    public ClientInfo(int maxPlayers, int minecraftPort, String versionString) {
        this.maxPlayers = maxPlayers;
        this.minecraftPort = minecraftPort;
        this.versionString = versionString;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinecraftPort() {
        return minecraftPort;
    }

    public String getVersionString() {
        return versionString;
    }

}
