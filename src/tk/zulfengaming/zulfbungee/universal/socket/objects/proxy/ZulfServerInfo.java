package tk.zulfengaming.zulfbungee.universal.socket.objects.proxy;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;

public class ZulfServerInfo<P> {

    private final SocketAddress socketAddress;
    private final ArrayList<ZulfProxyPlayer<P>> players = new ArrayList<>();

    public ZulfServerInfo(SocketAddress socketAddressIn, Collection<ZulfProxyPlayer<P>> playersIn) {
        this.socketAddress = socketAddressIn;
        players.addAll(playersIn);
    }

    public Collection<ZulfProxyPlayer<P>> getPlayers() {
        return players;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }
}
