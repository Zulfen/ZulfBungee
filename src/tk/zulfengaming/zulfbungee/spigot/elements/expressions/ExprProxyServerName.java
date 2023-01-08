package tk.zulfengaming.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;

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
        return zulfProxyServer.getName();
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }
}
