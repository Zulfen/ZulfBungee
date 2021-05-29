package tk.zulfengaming.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

// again, referenced code from Skungee 2.0!

public class ExprPlayerServer extends SimplePropertyExpression<ProxyPlayer, ProxyServer> {

    static {
        register(ExprPlayerServer.class, ProxyServer.class, "[(current|connected)] server[s]", "proxyplayers");
    }

    @Override
    protected String getPropertyName() {
        return "current server";
    }

    @Override
    public ProxyServer convert(ProxyPlayer proxyPlayer) {
        return proxyPlayer.getServer();
    }

    @Override
    public Class<? extends ProxyServer> getReturnType() {
        return ProxyServer.class;
    }
}
