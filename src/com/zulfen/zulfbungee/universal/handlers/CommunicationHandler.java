package com.zulfen.zulfbungee.universal.handlers;

import com.zulfen.zulfbungee.universal.interfaces.PacketConsumer;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.util.BlockingPacketQueue;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CommunicationHandler {

    protected final BlockingPacketQueue queueIn = new BlockingPacketQueue();
    protected final BlockingPacketQueue queueOut = new BlockingPacketQueue();

    private final PacketConsumer packetConsumer;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    public CommunicationHandler(PacketConsumer packetConsumerIn) {
        this.packetConsumer = packetConsumerIn;
    }

    public void dataInLoop() {
        while (isRunning.get()) {
            Optional<Packet> take = queueIn.take(false);
            take.ifPresent(packetConsumer::consume);
        }
    }

    public abstract Packet readPacketImpl();
    public abstract void writePacketImpl(Packet toWrite);

    protected void freeResources() {}

    public void destroy() {
        if (isRunning.compareAndSet(true, false)) {
            freeResources();
            packetConsumer.shutdown();
        }
    }

}
