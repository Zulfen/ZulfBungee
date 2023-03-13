package tk.zulfengaming.zulfbungee.universal.socket.objects.proxy;


import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;


import java.util.UUID;

public abstract class ZulfProxyPlayer<P> implements ProxyCommandSender<P> {

    protected final P platform;

    protected ZulfProxyPlayer(P platformIn) {
        this.platform = platformIn;
    }

    public abstract ZulfProxyServer<P> getServer();

    public abstract String getName();

    public abstract UUID getUuid();

    public abstract void connect(ZulfProxyServer<P> serverIn);

    public abstract void disconnect(String reason);

    @Override
    public String toString() {
        return String.format("ZulfProxyPlayer{name=%s, uuid=%s}", getName(), getUuid());
    }

}
