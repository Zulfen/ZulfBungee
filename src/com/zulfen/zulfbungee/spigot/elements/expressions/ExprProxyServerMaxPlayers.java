package com.zulfen.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.jetbrains.annotations.NotNull;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;

public class ExprProxyServerMaxPlayers extends SimplePropertyExpression<ClientServer, Number> {

    static {
        register(ExprProxyServerMaxPlayers.class, Number.class, "(player limit|max[imum] player count)", "proxyservers");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "proxy server player limit";
    }

    @Override
    public Number convert(ClientServer zulfProxyServer) {
        return zulfProxyServer.getClientInfo().getMaxPlayers();
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }
}
