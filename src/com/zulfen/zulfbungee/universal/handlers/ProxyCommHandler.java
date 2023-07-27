package com.zulfen.zulfbungee.universal.handlers;

import com.zulfen.zulfbungee.universal.ZulfBungeeProxy;
import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.managers.ProxyTaskManager;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

// issue must be here

public abstract class ProxyCommHandler<P, T> {

    protected ProxyServerConnection<P, T> connection;
    protected final ZulfBungeeProxy<P, T> pluginInstance;

    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    protected final LinkedBlockingQueue<Optional<Packet>> queueIn = new LinkedBlockingQueue<>();
    protected final LinkedBlockingQueue<Optional<Packet>> queueOut = new LinkedBlockingQueue<>();

    public ProxyCommHandler(ZulfBungeeProxy<P, T> pluginInstanceIn) {
        this.pluginInstance = pluginInstanceIn;
    }

    public void setServerConnection(ProxyServerConnection<P, T> connection) {
        this.connection = connection;
        ProxyTaskManager taskManager = pluginInstance.getTaskManager();
        taskManager.newTask(this::dataOutLoop);
    }

    private void dataOutLoop() {
        while (isRunning.get()) {
            try {
                Optional<Packet> possiblePacket = queueOut.take();
                if (possiblePacket.isPresent()) {
                    writePacket(possiblePacket.get());
                } else {
                    break;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public abstract Optional<Packet> readPacket();
    protected abstract void writePacket(Packet toWrite);

    protected void freeResources() {}

    public void destroy() {
        if (isRunning.compareAndSet(true, false)) {
            queueIn.offer(Optional.empty());
            queueOut.offer(Optional.empty());
            freeResources();
            connection.destroy();
        }
    }

    public void send(Packet packetIn) {
        queueOut.offer(Optional.of(packetIn));
    }


}
