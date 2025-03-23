package com.zulfen.zulfbungee.core.util;

import com.zulfen.zulfbungee.core.socket.objects.Packet;

import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.TimeUnit;

public class MultiplePacketQueue extends BlockingPacketQueue<LinkedTransferQueue<Object>> {

    public MultiplePacketQueue() {
        super(new LinkedTransferQueue<>());
    }

    @Override
    public void enqueue(Packet packet) {
        try {
            blockingQueue.tryTransfer(packet, 1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}