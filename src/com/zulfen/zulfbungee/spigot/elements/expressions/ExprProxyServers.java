package com.zulfen.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;

public class ExprProxyServers extends SimpleExpression<ClientServer> {

    static {
        Skript.registerExpression(ExprProxyServers.class, ClientServer.class, ExpressionType.SIMPLE, "[(all [[of] the]|the)] [online] [(proxy|bungeecord|bungee|velocity)] servers");
    }

    @Override
    protected ClientServer @NotNull [] get(@NotNull Event event) {
        return ZulfBungeeSpigot.getPlugin().getConnectionManager().getAllProxyServers().toArray(new ClientServer[0]);
    }

    @Override
    public boolean isSingle() {
       return false;
    }

    @Override
    public @NotNull Class<? extends ClientServer> getReturnType() {
        return ClientServer.class;
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "all online servers";
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        return true;
    }
}
