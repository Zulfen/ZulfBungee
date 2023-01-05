package tk.zulfengaming.zulfbungee.universal.socket.objects.proxy;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;

public class ZulfServerInfo<P> {

    private final SocketAddress socketAddress;
    private final Collection<ZulfProxyPlayer<P>> players;

    public ZulfServerInfo(SocketAddress socketAddressIn, Collection<ZulfProxyPlayer<P>> playersIn) {
        this.socketAddress = socketAddressIn;
        this.players = playersIn;
    }

    public Collection<ZulfProxyPlayer<P>> getPlayers() {
        return players;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }
}
