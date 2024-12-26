package com.zulfen.zulfbungee.universal.socket.transport;

import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.handlers.proxy.transport.ProxySocketCommHandler;
import com.zulfen.zulfbungee.universal.managers.MainServer;

import java.io.IOException;
import java.net.Socket;

public class SocketServerConnection<P, T, C> extends ProxyServerConnection<P, T, C> {

    public SocketServerConnection(MainServer<P, T, C> mainServerIn, Socket socketIn) throws IOException {
        super(mainServerIn, socketIn.getRemoteSocketAddress());
        setProxyCommHandler(new ProxySocketCommHandler<>(this, socketIn));
    }

}
