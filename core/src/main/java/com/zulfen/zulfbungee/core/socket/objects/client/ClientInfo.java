package com.zulfen.zulfbungee.core.socket.objects.client;

import java.io.Serializable;

public record ClientInfo(int maxPlayers, int minecraftPort, String versionString) implements Serializable {

}
