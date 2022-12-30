package tk.zulfengaming.zulfbungee.universal.socket.objects;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;

public class ServerInfo implements Serializable {

    private final SocketAddress socketAddress;
    private final ArrayList<ProxyPlayer> players = new ArrayList<>();

    public ServerInfo(SocketAddress socketAddressIn, Collection<ProxyPlayer> playersIn) {
        this.socketAddress = socketAddressIn;
        players.addAll(playersIn);
    }

    public Collection<ProxyPlayer> getPlayers() {
        return players;
    }

    public SocketAddress getSocketAddress() {
        return socketAddress;
    }
}
