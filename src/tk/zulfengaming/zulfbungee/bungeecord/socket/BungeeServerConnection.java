package tk.zulfengaming.zulfbungee.bungeecord.socket;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import tk.zulfengaming.zulfbungee.bungeecord.objects.BungeePlayer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.io.IOException;
import java.net.Socket;
import java.util.*;
import java.util.stream.Collectors;

public class BungeeServerConnection extends BaseServerConnection<ProxyServer> {

    public BungeeServerConnection(MainServer<ProxyServer> mainServerIn, Socket socketIn) throws IOException {
        super(mainServerIn, socketIn);
    }

    @Override
    public List<ZulfProxyPlayer<ProxyServer>> getPlayers() {

        Optional<String> nameFromAddress = getServer().getNameFromAddress(getAddress());

        if (nameFromAddress.isPresent()) {

            ServerInfo serverInfo = pluginInstance.getPlatform().getServers().get(nameFromAddress.get());

            return serverInfo.getPlayers().stream()
                    .filter(Objects::nonNull)
                    .map(BungeePlayer::new)
                    .collect(Collectors.toList());

        } else {
            return Collections.emptyList();
        }

    }

}
