package com.zulfen.zulfbungee.universal.socket.objects.client.skript;

import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;

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
