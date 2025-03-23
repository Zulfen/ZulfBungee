package com.zulfen.zulfbungee.core.socket.objects;

import java.io.Serializable;

public record ZulfByteBuffer(byte[] data) implements Serializable {

    public static ZulfByteBuffer emptyBuffer() {
        return new ZulfByteBuffer(new byte[0]);
    }


}
