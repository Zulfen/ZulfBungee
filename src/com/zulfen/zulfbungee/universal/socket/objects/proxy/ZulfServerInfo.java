package com.zulfen.zulfbungee.universal.socket.objects.proxy;

import java.net.SocketAddress;
import java.util.Collection;

public class ZulfServerInfo {

    private final SocketAddress socketAddress;

    public ZulfServerInfo(SocketAddress socketAddressIn) {
        this.socketAddress = socketAddressIn;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }
}
