package com.zulfen.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.jetbrains.annotations.NotNull;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientServer;

@Name("Proxy Server's Name")
@Description("The name of a given proxy server.")
public class ExprProxyServerName extends SimplePropertyExpression<ClientServer, String> {

    static {
        register(ExprProxyServerName.class, String.class, "name", "proxyservers");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "proxy server name";
    }

    @Override
    public String convert(ClientServer zulfProxyServer) {
        return zulfProxyServer.name();
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }
}
