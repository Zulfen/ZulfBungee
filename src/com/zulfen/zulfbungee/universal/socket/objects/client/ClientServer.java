package com.zulfen.zulfbungee.universal.socket.objects.client;

import java.io.Serializable;

public class ClientServer implements Serializable {

    private final String name;

    private final ClientInfo clientInfo;

    public ClientServer(String name, ClientInfo clientInfo) {
        this.name = name;
        this.clientInfo = clientInfo;
    }

    public String getName() {
        return name;
    }

    public ClientInfo getClientInfo() {
        return clientInfo;
    }

}
