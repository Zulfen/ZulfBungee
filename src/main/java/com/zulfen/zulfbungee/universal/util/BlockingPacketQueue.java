package com.zulfen.zulfbungee.universal.util;

import com.zulfen.zulfbungee.universal.socket.objects.Packet;

import java.util.Optional;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class BlockingPacketQueue<T extends BlockingQueue<Object>> implements PacketQueue {

    protected final T blockingQueue;
    private final Object shutdownFlag = new Object();
    private final Object interruptFlag = new Object();
    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    public BlockingPacketQueue(T blockingQueue) {
        this.blockingQueue = blockingQueue;
    }

    @Override
    public abstract void enqueue(Packet packet);

    @Override
    public Optional<Packet> take(boolean poll) {

        if (!shutdown.get()) {
            try {
                Object take;
                if (poll) {
                    take = blockingQueue.poll(500, TimeUnit.MILLISECONDS);
                } else {
                    take = blockingQueue.take();
                }

                if (take == null) {
                    return Optional.empty();
                }
                if (take.equals(shutdownFlag)) {
                    if (shutdown.compareAndSet(false, true)) {
                        return Optional.empty();
                    }
                }
                if (take.equals(interruptFlag)) {
                    return Optional.empty();
                }
                if (take instanceof Packet packet) {
                    return Optional.of(packet);
                }

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return Optional.empty();
        }

        return Optional.empty();

    }

    @Override
    public String toString() {
        return blockingQueue.toString();
    }

    public void notifyShutdown() {
        blockingQueue.clear();
        blockingQueue.offer(shutdownFlag);
    }

    public void notifyStopWaiting() {
        blockingQueue.clear();
        blockingQueue.offer(interruptFlag);
    }



}
