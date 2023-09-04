package com.zulfen.zulfbungee.universal.util;

import com.zulfen.zulfbungee.universal.socket.objects.Packet;

import java.util.Optional;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

public class BlockingPacketQueue {

    private final Object shutdownFlag = new Object();
    private final LinkedTransferQueue<Object> blockingQueue = new LinkedTransferQueue<>();

    public void offer(Packet packet) {
        try {
            blockingQueue.transfer(packet);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
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
        blockingQueue.tryTransfer(shutdownFlag);
        blockingQueue.clear();
    }

    @Override
    public String toString() {
        return blockingQueue.toString();
    }

}
