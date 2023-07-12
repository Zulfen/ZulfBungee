package tk.zulfengaming.zulfbungee.universal.interfaces;

import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.managers.ProxyTaskManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

// issue must be here

public abstract class ProxyCommHandler<P, T> {

    protected ProxyServerConnection<P, T> connection;
    protected final ZulfBungeeProxy<P, T> pluginInstance;

    private final AtomicBoolean isRunning = new AtomicBoolean(false);

    protected final LinkedBlockingQueue<Optional<Packet>> queueIn = new LinkedBlockingQueue<>();
    protected final LinkedBlockingQueue<Optional<Packet>> queueOut = new LinkedBlockingQueue<>();

    public ProxyCommHandler(ZulfBungeeProxy<P, T> pluginInstanceIn) {
        this.pluginInstance = pluginInstanceIn;
    }

    public void setServerConnection(ProxyServerConnection<P, T> connection) {
        this.connection = connection;
    }

    public void start() {
        if (isRunning.compareAndSet(false, true)) {
            ProxyTaskManager taskManager = pluginInstance.getTaskManager();
            taskManager.newTask(this::dataInLoop);
            taskManager.newTask(this::dataOutLoop);
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
