package com.zulfen.zulfbungee.universal.util;

import com.zulfen.zulfbungee.universal.socket.objects.Packet;

import java.util.Optional;

public interface PacketQueue {
    void enqueue(Packet packet);
    Optional<Packet> take(boolean poll);
}
