package tk.zulfengaming.zulfbungee.velocity.objects;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfServerInfo;
import tk.zulfengaming.zulfbungee.velocity.ZulfVelocity;

import java.util.List;
import java.util.stream.Collectors;

public class VelocityServer implements ZulfProxyServer<ProxyServer> {

    private final String name;

    private final ZulfServerInfo<ProxyServer> info;

    public VelocityServer(RegisteredServer velocityServerIn, ZulfVelocity pluginIn) {

        ProxyServer proxyServer = pluginIn.getVelocity();
        this.name = velocityServerIn.getServerInfo().getName();

        List<ZulfProxyPlayer<ProxyServer>> proxyPlayers = velocityServerIn.getPlayersConnected().stream()
                .map(player -> new VelocityPlayer(player, this, pluginIn))
                .collect(Collectors.toList());

        this.info = new ZulfServerInfo<>(proxyServer.getBoundAddress(), proxyPlayers);

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ZulfServerInfo<ProxyServer> getServerInfo() {
        return info;
    }
}
