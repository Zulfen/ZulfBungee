package tk.zulfengaming.zulfbungee.universal.socket.objects.proxy;

import java.net.SocketAddress;
import java.util.Collection;

public class ZulfServerInfo<P> {

    private final SocketAddress socketAddress;

    public ZulfServerInfo(SocketAddress socketAddressIn) {
        this.socketAddress = socketAddressIn;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }
}
