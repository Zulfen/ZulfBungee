package tk.zulfengaming.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.handlers.ProxyServerInfoManager;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.Arrays;

public class ExprOnlineServers extends SimpleExpression<ProxyServer> {

    static {
        Skript.registerExpression(ExprOnlineServers.class, ProxyServer.class, ExpressionType.SIMPLE, "[(all [[of] the]|the)] online [(proxy|bungeecord|bungee)] servers");
    }

    @Override
    protected ProxyServer @NotNull [] get(@NotNull Event event) {

        return ProxyServerInfoManager.getServers().toArray(new ProxyServer[0]);

    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends ProxyServer> getReturnType() {
        return ProxyServer.class;
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "all online servers: " + Arrays.toString(ProxyServerInfoManager.getServers().toArray(new ProxyServer[0]));
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        return true;
    }
}
