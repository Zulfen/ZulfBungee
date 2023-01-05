package tk.zulfengaming.zulfbungee.bungeecord.objects;

import net.md_5.bungee.api.ProxyServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfServerInfo;

import java.util.List;
import java.util.stream.Collectors;

public class BungeeServer implements ZulfProxyServer<ProxyServer> {

    private final String name;

    private final ZulfServerInfo<net.md_5.bungee.api.ProxyServer> info;

    public BungeeServer(net.md_5.bungee.api.config.ServerInfo bungeeInfoIn) {

        this.name = bungeeInfoIn.getName();

        List<ZulfProxyPlayer<ProxyServer>> proxyPlayers = bungeeInfoIn.getPlayers().stream()
                .map(proxiedPlayer -> new BungeePlayer<>(proxiedPlayer, this))
                .collect(Collectors.toList());

        this.info = new ZulfServerInfo<>(bungeeInfoIn.getSocketAddress(), proxyPlayers);

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ZulfServerInfo<net.md_5.bungee.api.ProxyServer> getServerInfo() {
        return info;
    }

}
