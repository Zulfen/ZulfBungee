package com.zulfen.zulfbungee.core.socket.objects.proxy;

import java.net.SocketAddress;

public class ZulfServerInfo {

    private final SocketAddress socketAddress;

    public ZulfServerInfo(SocketAddress socketAddressIn) {
        this.socketAddress = socketAddressIn;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }
}
