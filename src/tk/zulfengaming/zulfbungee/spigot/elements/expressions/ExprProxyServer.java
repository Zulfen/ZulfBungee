package tk.zulfengaming.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.Arrays;

public class ExprProxyServer extends SimpleExpression<ProxyServer> {

    private Expression<String> serverName;

    static {
        Skript.registerExpression(ExprProxyServer.class, ProxyServer.class, ExpressionType.SIMPLE, "(proxy|bungeecord|bungee) server %string%");
    }

    @Override
    protected ProxyServer[] get(Event event) {
        return new ProxyServer[] {new ProxyServer(serverName.getSingle(event))};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends ProxyServer> getReturnType() {
        return ProxyServer.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return "proxy server(s) " + Arrays.toString(serverName.getArray(event));
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        serverName = (Expression<String>) expressions[0];
        return true;
    }
}
