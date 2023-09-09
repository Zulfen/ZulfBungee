package com.zulfen.zulfbungee.spigot.interfaces;

import java.io.IOException;

public abstract class ConnectionFactory<T, M> {

    protected final M connectionManager;

    public ConnectionFactory(M connectionManagerIn) {
        this.connectionManager = connectionManagerIn;
    }

    public abstract T build() throws IOException;

}
