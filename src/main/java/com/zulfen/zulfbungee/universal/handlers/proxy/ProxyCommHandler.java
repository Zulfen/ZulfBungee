package com.zulfen.zulfbungee.universal.handlers.proxy;

import com.zulfen.zulfbungee.universal.ZulfProxyImpl;
import com.zulfen.zulfbungee.universal.handlers.CommunicationHandler;
import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;

// issue must be here

public abstract class ProxyCommHandler<P, T, C> extends CommunicationHandler {

    protected final ZulfProxyImpl<P, T, C> pluginInstance;

    public ProxyCommHandler(ProxyServerConnection<P, T, C> connectionIn) {
        super(connectionIn);
        this.pluginInstance = connectionIn.getPluginInstance();
    }

}
