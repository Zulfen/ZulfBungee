package tk.zulfengaming.zulfbungee.bungeecord.socket;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import tk.zulfengaming.zulfbungee.bungeecord.objects.BungeePlayer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
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

            String name = nameFromAddress.get();

            ServerInfo serverInfo = pluginInstance.getPlatform().getServers().get(name);

            if (serverInfo != null) {
                return serverInfo.getPlayers().stream()
                        .filter(Objects::nonNull)
                        .map(BungeePlayer::new)
                        .collect(Collectors.toList());
            } else {
               pluginInstance.warning(String.format("Please make sure that forced-connection-name (%s) for %s matches the name defined in your proxy's server software config, as the server couldn't be found.", name,
                       getAddress()));
               sendDirect(new Packet(PacketTypes.INVALID_CONFIGURATION, false, true, new Object[0]));
            }

        } else {
            return Collections.emptyList();
        }

        return Collections.emptyList();

    }

}
