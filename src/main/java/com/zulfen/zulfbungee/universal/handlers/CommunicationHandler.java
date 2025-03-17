package com.zulfen.zulfbungee.universal.handlers;

import com.zulfen.zulfbungee.universal.interfaces.PacketConsumer;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.util.MultiplePacketQueue;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CommunicationHandler {

    protected final MultiplePacketQueue queueIn = new MultiplePacketQueue();
    protected final MultiplePacketQueue queueOut = new MultiplePacketQueue();

    protected final PacketConsumer packetConsumer;
    protected final AtomicBoolean isRunning = new AtomicBoolean(true);

    public CommunicationHandler(PacketConsumer connection) {
        this.packetConsumer = connection;
    }

    public void dataInLoop() {
        while (isRunning.get()) {
            Optional<Packet> packet = readPacketImpl();
            packet.ifPresent(queueIn::enqueue);
        }
    }

    public void processLoop() {
        while (isRunning.get()) {
            Optional<Packet> take = queueIn.take(false);
            if (take.isPresent()) {
                Packet packet = take.get();
                packetConsumer.consume(packet);
            }
        }
    }

    public void dataOutLoop() {
        while (isRunning.get()) {
            Optional<Packet> take = queueOut.take(false);
            take.ifPresent(this::writePacketImpl);
        }
    }

    public void enqueuePacket(Packet packet) {
        queueOut.enqueue(packet);
    }

    public abstract Optional<Packet> readPacketImpl();
    public abstract void writePacketImpl(Packet toWrite);

    protected abstract void freeResources();

    public void destroy() {
        if (isRunning.compareAndSet(true, false)) {
            freeResources();
            queueOut.notifyShutdown();
            queueIn.notifyShutdown();
            packetConsumer.destroyConsumer();
        }
    }

}
