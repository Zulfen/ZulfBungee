package tk.zulfengaming.zulfbungee.universal.socket.objects.proxy;


import java.net.SocketAddress;
import java.util.Collections;
import java.util.List;

public abstract class ZulfProxyServer<P, T> {

    private final String name;
    private final SocketAddress socketAddress;

    public ZulfProxyServer(String nameIn, SocketAddress addressIn) {
        this.name = nameIn;
        this.socketAddress = addressIn;
    }

    public String getName() {
        return name;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }

    // we do this when requested instead of in the constructor for performance
    public abstract List<ZulfProxyPlayer<P, T>> getPlayers();

    public abstract boolean sendData(String channelNameIn, byte[] dataOut);

}
