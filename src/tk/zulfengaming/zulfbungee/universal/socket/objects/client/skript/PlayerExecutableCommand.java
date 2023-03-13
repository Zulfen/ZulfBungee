package tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript;

import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;

import java.io.Serializable;

public class PlayerExecutableCommand implements Serializable {

    private final ClientPlayer[] players;
    private final String command;

    public PlayerExecutableCommand(ClientPlayer[] players, String command) {
        this.players = players;
        this.command = command;
    }

    public ClientPlayer[] getPlayers() {
        return players;
    }

    public String getCommand() {
        return command;
    }

}