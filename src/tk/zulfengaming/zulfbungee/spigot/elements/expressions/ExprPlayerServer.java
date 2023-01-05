package tk.zulfengaming.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;

import java.util.Optional;

// again, referenced code from Skungee 2.0!

public class ExprPlayerServer extends SimplePropertyExpression<ClientPlayer, ClientServer> {

    static {
        register(ExprPlayerServer.class, ClientServer.class, "[(current|connected)] server", "proxyplayers");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "current server";
    }

    @Override
    public ClientServer convert(ClientPlayer proxyPlayer) {

        ClientConnection connection = ZulfBungeeSpigot.getPlugin().getConnection();

        Optional<Packet> send = connection.send(new Packet(PacketTypes.PLAYER_SERVER, true, false, proxyPlayer));

        if (send.isPresent()) {

            Packet packetIn = send.get();

            if (packetIn.getDataArray().length != 0) {

                Optional<ClientServer> optionalProxyServer = connection.getProxyServer((String) packetIn.getDataSingle());
                if (optionalProxyServer.isPresent()) {
                    return optionalProxyServer.get();
                }

            }

        }

        return null;
    }

    @Override
    public Class<? extends ClientServer> getReturnType() {
        return ClientServer.class;
    }
}
