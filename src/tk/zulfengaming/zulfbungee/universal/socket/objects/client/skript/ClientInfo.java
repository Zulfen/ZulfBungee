package tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript;

public class ClientInfo {

    private final int maxPlayers;

    private final int minecraftPort;

    public ClientInfo(int maxPlayers, int minecraftPort) {
        this.maxPlayers = maxPlayers;
        this.minecraftPort = minecraftPort;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getMinecraftPort() {
        return minecraftPort;
    }
}
