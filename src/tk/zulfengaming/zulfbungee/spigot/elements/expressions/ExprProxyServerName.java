package tk.zulfengaming.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

public class ExprProxyServerName extends SimplePropertyExpression<ProxyServer, String> {

    static {
        register(ExprProxyServerName.class, String.class, "name", "proxyservers");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "proxy server name";
    }

    @Override
    public String convert(ProxyServer proxyServer) {
        return proxyServer.getName();
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }
}
