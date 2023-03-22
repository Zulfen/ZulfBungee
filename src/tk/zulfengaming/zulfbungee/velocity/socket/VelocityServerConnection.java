package tk.zulfengaming.zulfbungee.velocity.socket;

import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.velocity.ZulfVelocity;
import tk.zulfengaming.zulfbungee.velocity.objects.VelocityPlayer;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class VelocityServerConnection extends BaseServerConnection<ProxyServer> {

    public VelocityServerConnection(MainServer<ProxyServer> mainServerIn, Socket socketIn) throws IOException {
        super(mainServerIn, socketIn);
    }


    @Override
    public List<ZulfProxyPlayer<ProxyServer>> getPlayers() {

        Optional<String> nameFromAddress = getServer().getNameFromAddress(getAddress());

        if (nameFromAddress.isPresent()) {

            Optional<RegisteredServer> server = pluginInstance.getPlatform().getServer(nameFromAddress.get());
            return server.<List<ZulfProxyPlayer<ProxyServer>>>map(registeredServer -> registeredServer.getPlayersConnected().stream()
                    .map(player -> new VelocityPlayer(player, (ZulfVelocity) pluginInstance))
                    .collect(Collectors.toList())).orElse(Collections.emptyList());

        }

        return Collections.emptyList();

    }

}
