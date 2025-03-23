package com.zulfen.zulfbungee.core.util;

import com.zulfen.zulfbungee.core.socket.objects.Packet;

import java.util.Optional;

public interface PacketQueue {
    void enqueue(Packet packet);
    Optional<Packet> take(boolean poll);
}
