package com.zulfen.zulfbungee.spigot.interfaces;

import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.socket.ClientConnection;
import com.zulfen.zulfbungee.universal.handlers.CommunicationHandler;

import java.util.concurrent.CountDownLatch;

// issue must be here

public abstract class ClientCommHandler<T> extends CommunicationHandler {

    protected ClientConnection<T> connection;
    protected final ZulfBungeeSpigot pluginInstance;

    // Warning - RELEASE THIS SOMEWHERE!
    protected final CountDownLatch awaitProperConnection = new CountDownLatch(1);

    public ClientCommHandler(ClientConnection<T> connectionIn) {
        super(connectionIn);
        this.pluginInstance = connectionIn.getPluginInstance();
    }

    public void setConnection(ClientConnection<T> connection) {
        this.connection = connection;
    }

    public void destroy() {
        awaitProperConnection.countDown();
        connection.destroy();
        super.destroy();
    }

    public void awaitInitialConnection() {
        try {
            awaitProperConnection.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void signalInitialConnection() {
        awaitProperConnection.countDown();
    }

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
    }


}
