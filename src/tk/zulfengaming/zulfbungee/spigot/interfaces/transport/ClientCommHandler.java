package tk.zulfengaming.zulfbungee.spigot.interfaces.transport;

import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.managers.TaskManager;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.LinkedTransferQueue;
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
            pluginInstance.error("called again");
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
