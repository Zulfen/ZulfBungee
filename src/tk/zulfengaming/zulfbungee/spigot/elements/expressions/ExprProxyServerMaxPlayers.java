package tk.zulfengaming.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

public class ExprProxyServerMaxPlayers extends SimplePropertyExpression<ProxyServer, Number> {

    static {
        register(ExprProxyServerMaxPlayers.class, Number.class, "(player limit|max player count)", "proxyservers");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "proxy server player limit";
    }

    @Override
    public Number convert(ProxyServer proxyServer) {
        return proxyServer.getServerInfo().getMaxPlayers();
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }
}
