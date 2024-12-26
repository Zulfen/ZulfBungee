package com.zulfen.zulfbungee.universal.socket.transport;

import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.handlers.proxy.transport.ProxyChannelCommHandler;
import com.zulfen.zulfbungee.universal.interfaces.MessageCallback;
import com.zulfen.zulfbungee.universal.managers.MainServer;

import java.net.SocketAddress;

public class ChannelServerConnection<P, T, C> extends ProxyServerConnection<P, T, C> {

    private final ProxyChannelCommHandler<P, T, C> proxyChannelCommHandler;

    public ChannelServerConnection(MainServer<P, T, C> mainServerIn, MessageCallback messageCallbackIn, SocketAddress socketAddressIn) {
        super(mainServerIn, socketAddressIn);
        this.proxyChannelCommHandler = new ProxyChannelCommHandler<>(this, messageCallbackIn);
        setProxyCommHandler(proxyChannelCommHandler);
    }

    public ProxyChannelCommHandler<P, T, C> getProxyChannelCommHandler() {
        return proxyChannelCommHandler;
    }

}
