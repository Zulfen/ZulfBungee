package com.zulfen.zulfbungee.universal.interfaces;

import com.zulfen.zulfbungee.universal.socket.objects.Packet;

public interface PacketConsumer {
    void consume(Packet packetIn);
    void shutdown();
}
