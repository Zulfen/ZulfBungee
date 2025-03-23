package com.zulfen.zulfbungee.core.interfaces;

import com.zulfen.zulfbungee.core.socket.objects.Packet;

public interface PacketConsumer {
    void consume(Packet packetIn);
    void destroyConsumer();
}
