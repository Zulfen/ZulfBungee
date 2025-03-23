package com.zulfen.zulfbungee.core.handlers.proxy;

import com.zulfen.zulfbungee.core.ZulfProxyImpl;
import com.zulfen.zulfbungee.core.handlers.CommunicationHandler;
import com.zulfen.zulfbungee.core.socket.ProxyServerConnection;

// issue must be here

public abstract class ProxyCommHandler<P, T, C> extends CommunicationHandler {

    protected final ZulfProxyImpl<P, T, C> pluginInstance;

    public ProxyCommHandler(ProxyServerConnection<P, T, C> connectionIn) {
        super(connectionIn);
        this.pluginInstance = connectionIn.getPluginInstance();
    }

}
