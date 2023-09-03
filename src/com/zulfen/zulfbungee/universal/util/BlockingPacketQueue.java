package com.zulfen.zulfbungee.universal.util;

import com.zulfen.zulfbungee.universal.socket.objects.Packet;

import java.util.Optional;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

public class BlockingPacketQueue {

    private final Object shutdownFlag = new Object();
    private final LinkedBlockingDeque<Object> blockingQueue = new LinkedBlockingDeque<>();

    public void offer(Packet packet) {
        blockingQueue.offerLast(packet);
    }

    public Optional<Packet> take(boolean poll) {

        try {

            Object take;
            if (poll) {
                take = blockingQueue.poll(500, TimeUnit.MILLISECONDS);
            } else {
                take = blockingQueue.take();
            }

            if (take != null) {
                if (take.equals(shutdownFlag)) {
                    return Optional.empty();
                } else if (take instanceof Packet) {
                    Packet packet = (Packet) take;
                    return Optional.of(packet);
                }
            }

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return Optional.empty();

    }

    public void notifyListeners() {
        blockingQueue.offerLast(shutdownFlag);
        blockingQueue.clear();
    }

    @Override
    public String toString() {
        return blockingQueue.toString();
    }

}
