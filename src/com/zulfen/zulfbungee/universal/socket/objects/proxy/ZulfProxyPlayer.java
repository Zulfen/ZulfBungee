package com.zulfen.zulfbungee.universal.socket.objects.proxy;


import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;

import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

public abstract class ZulfProxyPlayer<P, T> implements ProxyCommandSender<P, T> {

    protected final P platform;
    protected final String name;
    protected final UUID uuid;

    private final ZulfProxyServer<P, T> server;
    private final InetSocketAddress address;

    protected ZulfProxyPlayer(P platformIn, String nameIn, UUID uuidIn, InetSocketAddress addressIn, ZulfProxyServer<P, T> serverIn) {
        this.platform = platformIn;
        this.name = nameIn;
        this.uuid = uuidIn;
        this.address = addressIn;
        this.server = serverIn;
    }

    public ZulfProxyServer<P, T> getServer() {
        return server;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public InetSocketAddress getSocketAddress() {
        return address;
    }

    public abstract Optional<InetSocketAddress> getVirtualHost();

    @Override
    public boolean isPlayer() {
        return true;
    }

    public abstract void connect(ZulfProxyServer<P, T> serverIn);

    public abstract void disconnect(String reason);

    @Override
    public String toString() {
        return String.format("ZulfProxyPlayer{name=%s, uuid=%s}", getName(), getUuid());
    }

}
