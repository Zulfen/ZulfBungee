package com.zulfen.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
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
        return optionalVirtHost.map(inetSocketAddress -> inetSocketAddress.getAddress().toString()).orElse(null);
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
