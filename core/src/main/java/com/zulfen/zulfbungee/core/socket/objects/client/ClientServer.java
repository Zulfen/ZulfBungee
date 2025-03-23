package com.zulfen.zulfbungee.core.socket.objects.client;

import java.io.Serial;
import java.io.Serializable;

public record ClientServer(String name, ClientInfo clientInfo) implements Serializable {
    @Serial
    private static final long serialVersionUID = 0L;

    @Override
    public String toString() {
        return "ClientServer[" +
                "name=" + name + ", " +
                "clientInfo=" + clientInfo + ']';
    }

}
