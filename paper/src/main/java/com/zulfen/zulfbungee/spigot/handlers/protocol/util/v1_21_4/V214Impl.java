package com.zulfen.zulfbungee.spigot.handlers.protocol.util.v1_21_4;

import com.zulfen.zulfbungee.spigot.handlers.protocol.util.v1_20_2.V202Impl;

public class V214Impl extends V202Impl {

    @Override
    protected Class<?> getPayloadType() {
        return byte[].class;
    }

    // no need for conversion as we are on 1.21, and we expect a byte array
    @Override
    protected Object convertBytes(byte[] bytes) {
        return bytes;
    }

}
