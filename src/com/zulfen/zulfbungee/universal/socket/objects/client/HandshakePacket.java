package com.zulfen.zulfbungee.universal.socket.objects.client;

import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;

public class HandshakePacket extends Packet {

    public HandshakePacket(PacketTypes packetType, Object dataIn) {
        super(packetType, true, true, dataIn);
    }

    public HandshakePacket(PacketTypes packetType, Object[] dataIn) {
        super(packetType, true, true, dataIn);
    }

}
