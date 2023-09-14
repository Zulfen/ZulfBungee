package com.zulfen.zulfbungee.universal.handlers.proxy;

import com.zulfen.zulfbungee.universal.ZulfProxyImpl;
import com.zulfen.zulfbungee.universal.handlers.CommunicationHandler;
import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;

// issue must be here

public abstract class ProxyCommHandler<P, T> extends CommunicationHandler {

    protected final ZulfProxyImpl<P, T> pluginInstance;

    public ProxyCommHandler(ProxyServerConnection<P, T> connectionIn) {
        super(connectionIn);
        this.pluginInstance = connectionIn.getPluginInstance();
    }

}
