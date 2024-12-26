package com.zulfen.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.jetbrains.annotations.NotNull;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.managers.ConnectionManager;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;

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

        Optional<ClientServer> serverOptional = proxyPlayer.getServer();

        if (serverOptional.isPresent()) {

            return serverOptional.get();

        } else {

            ConnectionManager<?> connection = ZulfBungeeSpigot.getPlugin().getConnectionManager();
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


        }

        return null;
    }

    @Override
    public Class<? extends ClientServer> getReturnType() {
        return ClientServer.class;
    }
}
