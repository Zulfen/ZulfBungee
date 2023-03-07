package tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript;

import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;

import java.io.Serializable;

public class ConsoleExecutableCommand implements Serializable {

    private final ClientServer[] players;
    private final String command;

    public ConsoleExecutableCommand(ClientServer[] servers, String command) {
        this.players = servers;
        this.command = command;
    }

    public ClientServer[] getServers() {
        return players;
    }

    public String getCommand() {
        return command;
    }

}
