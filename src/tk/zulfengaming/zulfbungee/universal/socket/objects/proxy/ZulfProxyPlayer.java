package tk.zulfengaming.zulfbungee.universal.socket.objects.proxy;


import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;

import java.util.UUID;

public abstract class ZulfProxyPlayer<P, T> implements ProxyCommandSender<P, T> {

    protected final P platform;
    protected final String name;
    protected final UUID uuid;

    private final ZulfProxyServer<P, T> server;

    protected ZulfProxyPlayer(P platformIn, String nameIn, UUID uuidIn, ZulfProxyServer<P, T> serverIn) {
        this.platform = platformIn;
        this.name = nameIn;
        this.uuid = uuidIn;
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

    public abstract void connect(ZulfProxyServer<P, T> serverIn);

    public abstract void disconnect(String reason);

    @Override
    public String toString() {
        return String.format("ZulfProxyPlayer{name=%s, uuid=%s}", getName(), getUuid());
    }

}
