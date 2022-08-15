package tk.zulfengaming.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.managers.ProxyServerInfoManager;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.Optional;

// again, referenced code from Skungee 2.0!

public class ExprPlayerServer extends SimplePropertyExpression<ProxyPlayer, ProxyServer> {

    static {
        register(ExprPlayerServer.class, ProxyServer.class, "[(current|connected)] server", "proxyplayers");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "current server";
    }

    @Override
    public ProxyServer convert(ProxyPlayer proxyPlayer) {

        ClientConnection connection = ZulfBungeeSpigot.getPlugin().getConnection();

        Optional<Packet> send = connection.send(new Packet(PacketTypes.PLAYER_SERVER, true, false, proxyPlayer));

        if (send.isPresent()) {

            Packet packetIn = send.get();

            if (packetIn.getDataArray().length != 0) {

                Optional<ProxyServer> optionalProxyServer = ProxyServerInfoManager.toProxyServer((String) packetIn.getDataSingle());

                if (optionalProxyServer.isPresent()) {
                    return optionalProxyServer.get();
                }

            }

        }

        return null;
    }

    @Override
    public @NotNull Class<? extends ProxyServer> getReturnType() {
        return ProxyServer.class;
    }
}
