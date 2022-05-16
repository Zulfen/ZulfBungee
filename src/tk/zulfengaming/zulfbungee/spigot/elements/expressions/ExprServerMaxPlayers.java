package tk.zulfengaming.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.handlers.ProxyServerInfoManager;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

public class ExprServerMaxPlayers extends SimplePropertyExpression<ProxyServer, Number> {

    static {
        register(ExprServerMaxPlayers.class, Number.class, "(player limit|max player count)", "proxyservers");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "player limit";
    }

    @Override
    public Number convert(ProxyServer proxyServer) {
        return ProxyServerInfoManager.getClientInfo(proxyServer.getName()).getMaxPlayers();
    }

    @Override
    public @NotNull Class<? extends Number> getReturnType() {
        return Number.class;
    }
}
