package com.zulfen.zulfbungee.universal.socket.objects.client.skript;

import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;

import java.io.Serializable;

public class ServerMessage implements Serializable {

    private final Value[] values;

    private final String title;

    private final ClientServer[] servers;

    private final ClientServer from;

    public ServerMessage(String titleIn, Value[] valuesIn, ClientServer[] serversIn, ClientServer fromIn) {
        this.title = titleIn;
        this.values = valuesIn;
        this.servers = serversIn;
        this.from = fromIn;
    }

    public String getTitle() {
        return title;
    }

    public Value[] getData() {
        return values;
    }

    public ClientServer[] getServers() {
        return servers;
    }

    public ClientServer getFrom() {
        return from;
    }

}
