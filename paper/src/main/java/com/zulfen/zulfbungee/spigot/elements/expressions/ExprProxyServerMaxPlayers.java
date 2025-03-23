package com.zulfen.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.jetbrains.annotations.NotNull;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientServer;

@Name("Proxy Server's Max Player Count")
@Description("The maximum number of players that can join a given proxy server.")
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
        return zulfProxyServer.clientInfo().maxPlayers();
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }
}
