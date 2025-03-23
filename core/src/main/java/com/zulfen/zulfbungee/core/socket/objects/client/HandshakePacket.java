package com.zulfen.zulfbungee.core.socket.objects.client;

import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.PacketTypes;

public class HandshakePacket extends Packet {

    public HandshakePacket(PacketTypes packetType, Object dataIn) {
        super(packetType, true, true, dataIn);
    }

    public HandshakePacket(PacketTypes packetType, Object[] dataIn) {
        super(packetType, true, true, dataIn);
    }

}
