package com.zulfen.zulfbungee.spigot.interfaces.transport;

import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.spigot.managers.TaskManager;
import com.zulfen.zulfbungee.spigot.socket.Connection;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

// issue must be here

public abstract class ClientCommHandler {

    protected Connection connection;
    protected final ZulfBungeeSpigot pluginInstance;

    protected final AtomicBoolean isRunning = new AtomicBoolean(true);

    protected final LinkedBlockingQueue<Optional<Packet>> queueIn = new LinkedBlockingQueue<>();
    protected final LinkedBlockingQueue<Optional<Packet>> queueOut = new LinkedBlockingQueue<>();

    public ClientCommHandler(ZulfBungeeSpigot pluginInstanceIn) {
        this.pluginInstance = pluginInstanceIn;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }


    public void start() {
        TaskManager taskManager = pluginInstance.getTaskManager();
        taskManager.newAsyncTask(this::dataOutLoop);
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
        try {
            queueOut.put(Optional.of(packetIn));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
    }


}
