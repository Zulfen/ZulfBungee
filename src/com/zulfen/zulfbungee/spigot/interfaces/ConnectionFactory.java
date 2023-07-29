package com.zulfen.zulfbungee.spigot.interfaces;

public abstract class ConnectionFactory<T, M> {

    protected final M connectionManager;

    public ConnectionFactory(M connectionManagerIn) {
        this.connectionManager = connectionManagerIn;
    }

    public abstract T build();

}
