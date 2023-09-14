package com.zulfen.zulfbungee.universal.handlers;

import com.zulfen.zulfbungee.universal.interfaces.PacketConsumer;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.util.BlockingPacketQueue;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class CommunicationHandler {

    protected final BlockingPacketQueue queueIn = new BlockingPacketQueue();
    protected final BlockingPacketQueue queueOut = new BlockingPacketQueue();

    protected final PacketConsumer packetConsumer;
    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    public CommunicationHandler(PacketConsumer packetConsumerIn) {
        this.packetConsumer = packetConsumerIn;
    }

    public void dataInLoop() {
        while (isRunning.get()) {
            Optional<Packet> packet = readPacketImpl();
            packet.ifPresent(queueIn::offer);
        }
    }

    public void processLoop() {
        while (isRunning.get()) {
            Optional<Packet> take = queueIn.take(false);
            if (take.isPresent()) {
                packetConsumer.consume(take.get());
            } else {
                packetConsumer.destroyConsumer();
            }
        }
    }

    public void dataOutLoop() {
        while (isRunning.get()) {
            Optional<Packet> take = queueOut.take(false);
            take.ifPresent(this::writePacketImpl);
        }
    }

    public void offerPacket(Packet packetIn) {
        queueOut.offer(packetIn);
    }

    public abstract Optional<Packet> readPacketImpl();
    public abstract void writePacketImpl(Packet toWrite);

    protected void freeResources() {}

    public void destroy() {
        if (isRunning.compareAndSet(true, false)) {
            freeResources();
            queueIn.notifyListeners();
            queueOut.notifyListeners();
            packetConsumer.destroyConsumer();
        }
    }

}
