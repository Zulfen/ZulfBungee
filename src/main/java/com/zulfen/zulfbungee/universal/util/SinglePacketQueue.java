package com.zulfen.zulfbungee.universal.util;

import com.zulfen.zulfbungee.universal.socket.objects.Packet;

import java.util.concurrent.ArrayBlockingQueue;

public class SinglePacketQueue extends BlockingPacketQueue<ArrayBlockingQueue<Object>> {

    public SinglePacketQueue() {
        super(new ArrayBlockingQueue<>(1, true));
    }

    @Override
    public void enqueue(Packet packet) {
        try {
            blockingQueue.put(packet);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


}
