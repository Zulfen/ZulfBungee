package com.zulfen.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;

import java.util.Optional;

public class ExprCurrentProxyServer extends SimpleExpression<ClientServer> {

    static {
        Skript.registerExpression(ExprCurrentProxyServer.class, ClientServer.class, ExpressionType.SIMPLE, "this [script's] (server|client|proxy server)");
    }

    @Override
    protected ClientServer[] get(@NotNull Event event) {
        Optional<ClientServer> serverOptional = ZulfBungeeSpigot.getPlugin().getConnectionManager().getAsServer();
        return serverOptional.map(CollectionUtils::array).orElse(null);
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends ClientServer> getReturnType() {
        return ClientServer.class;
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "current proxy server name";
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        return true;
    }
}
