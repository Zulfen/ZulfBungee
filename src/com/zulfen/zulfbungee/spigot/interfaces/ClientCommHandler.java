package com.zulfen.zulfbungee.spigot.interfaces;

import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.socket.ClientConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

// issue must be here

public abstract class ClientCommHandler<T> {

    protected ClientConnection<T> connection;
    protected final ZulfBungeeSpigot pluginInstance;

    // Warning - RELEASE THIS SOMEWHERE!
    protected final CountDownLatch awaitProperConnection = new CountDownLatch(1);
    protected final AtomicBoolean isRunning = new AtomicBoolean(true);

    public ClientCommHandler(ZulfBungeeSpigot pluginInstanceIn) {
        this.pluginInstance = pluginInstanceIn;
    }

    public void setConnection(ClientConnection<T> connection) {
        this.connection = connection;
    }

    public abstract Optional<Packet> readPacket();
    public abstract void writePacket(Packet toWrite);

    protected void freeResources() {}

    public void destroy() {
        if (isRunning.compareAndSet(true, false)) {
            awaitProperConnection.countDown();
            freeResources();
            connection.destroy();
        }
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
