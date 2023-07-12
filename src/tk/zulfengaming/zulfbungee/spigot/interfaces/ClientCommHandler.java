package tk.zulfengaming.zulfbungee.spigot.interfaces;

import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.managers.TaskManager;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

// issue must be here

public abstract class ClientCommHandler {

    protected Connection connection;
    protected final ZulfBungeeSpigot pluginInstance;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    protected final LinkedBlockingQueue<Optional<Packet>> queueIn = new LinkedBlockingQueue<>();
    protected final LinkedBlockingQueue<Optional<Packet>> queueOut = new LinkedBlockingQueue<>();

    public ClientCommHandler(ZulfBungeeSpigot pluginInstanceIn) {
        this.pluginInstance = pluginInstanceIn;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }


    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            TaskManager taskManager = pluginInstance.getTaskManager();
            taskManager.newAsyncTask(this::dataInLoop);
            taskManager.newAsyncTask(this::dataOutLoop);
        }
    }

    private void dataInLoop() {
        while (isRunning.get()) {
            try {
                queueIn.put(readPacket());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void dataOutLoop() {
        while (isRunning.get()) {
            try {
                Optional<Packet> possiblePacket = queueOut.take();
                possiblePacket.ifPresent(this::writePacket);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    protected abstract Optional<Packet> readPacket();
    protected abstract void writePacket(Packet toWrite);

    protected void freeResources() {}

    public void destroy() {
        if (isRunning.compareAndSet(true, false)) {
            queueIn.offer(Optional.empty());
            freeResources();
            connection.destroy();
        }
    }

    public void send(Packet packetIn) {
        queueOut.offer(Optional.of(packetIn));
    }


    public Optional<Packet> read() {
        try {
            return queueIn.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return Optional.empty();
    }


}
