package tk.zulfengaming.zulfbungee.velocity.objects;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import tk.zulfengaming.zulfbungee.velocity.ZulfVelocity;

import java.util.List;
import java.util.stream.Collectors;

public class VelocityServer extends ZulfProxyServer<ProxyServer, Player> {

    private final RegisteredServer server;
    private final ZulfVelocity pluginInstance;

    public VelocityServer(RegisteredServer velocityServerIn, ZulfVelocity pluginIn) {
        super(velocityServerIn.getServerInfo().getName(), velocityServerIn.getServerInfo().getAddress());
        this.server = velocityServerIn;
        this.pluginInstance = pluginIn;
    }

    @Override
    public List<ZulfProxyPlayer<ProxyServer, Player>> getPlayers() {
        return server.getPlayersConnected().stream()
                .map(player -> new VelocityPlayer(player, this, pluginInstance))
                .collect(Collectors.toList());
    }

    @Override
    public boolean sendData(String channelNameIn, byte[] dataOut) {
        return server.sendPluginMessage(MinecraftChannelIdentifier.from(channelNameIn), dataOut);
    }


}
