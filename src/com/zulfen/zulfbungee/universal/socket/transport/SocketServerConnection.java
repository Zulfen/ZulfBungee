package com.zulfen.zulfbungee.universal.socket.transport;

import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.handlers.proxy.transport.ProxySocketCommHandler;
import com.zulfen.zulfbungee.universal.managers.MainServer;

import java.io.IOException;
import java.net.Socket;

public class SocketServerConnection<P, T> extends ProxyServerConnection<P, T> {

    public SocketServerConnection(MainServer<P, T> mainServerIn, Socket socketIn) throws IOException {
        super(mainServerIn, socketIn.getRemoteSocketAddress());
        setProxyCommHandler(new ProxySocketCommHandler<>(this, socketIn));
    }

}
