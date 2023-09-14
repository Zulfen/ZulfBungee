package com.zulfen.zulfbungee.universal.handlers;

import com.zulfen.zulfbungee.universal.ZulfProxyImpl;
import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.util.BlockingPacketQueue;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

// issue must be here

public abstract class ProxyCommHandler<P, T> {

    protected ProxyServerConnection<P, T> connection;
    protected final ZulfProxyImpl<P, T> pluginInstance;

    protected final BlockingPacketQueue queueIn = new BlockingPacketQueue();
    protected final BlockingPacketQueue queueOut = new BlockingPacketQueue();

    private final AtomicBoolean isRunning = new AtomicBoolean(true);

    public ProxyCommHandler(ZulfProxyImpl<P, T> pluginInstanceIn) {
        this.pluginInstance = pluginInstanceIn;
    }

    public void setServerConnection(ProxyServerConnection<P, T> connection) {
        this.connection = connection;
    }

    public void dataInLoop() {
        while (isRunning.get()) {
            Optional<Packet> take = queueIn.take(false);
            take.ifPresent(packet -> connection.processPacket(packet));
        }
    }
    
    

    public abstract Packet readPacketImpl();
    public abstract void writePacket(Packet toWrite);

    protected void freeResources() {}

    public void destroy() {
        if (isRunning.compareAndSet(true, false)) {
            freeResources();
            connection.destroy();
        }
    }


}
