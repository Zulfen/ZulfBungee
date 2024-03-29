package com.zulfen.zulfbungee.universal.socket.objects;

import java.io.Serializable;

public class ZulfByteBuffer implements Serializable {

    private final byte[] data;

    public ZulfByteBuffer(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public static ZulfByteBuffer emptyBuffer() {
        return new ZulfByteBuffer(new byte[0]);
    }



}
