package com.zulfen.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import org.jetbrains.annotations.NotNull;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;

import java.net.InetSocketAddress;
import java.util.Optional;

public class ExprProxyPlayerVirtualHost extends SimplePropertyExpression<ClientPlayer, String> {

    static {
        register(ExprProxyPlayerVirtualHost.class, String.class,"virtual host", "proxyplayers");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "virtual host";
    }

    @Override
    public String convert(ClientPlayer clientPlayer) {
        Optional<InetSocketAddress> optionalVirtHost = clientPlayer.getVirtualHost();
        if (optionalVirtHost.isPresent()) {
            return optionalVirtHost.get().getHostString();
        } else {
            Optional<Packet> send = ZulfBungeeSpigot.getPlugin().getConnectionManager().send(new Packet(PacketTypes.PLAYER_VIRTUAL_HOST, true, true,
                    clientPlayer));
            if (send.isPresent()) {
                Object[] dataArray = send.get().getDataArray();
                if (dataArray.length > 0) {
                    return (String) dataArray[0];
                }
            }
        }
        return null;
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
