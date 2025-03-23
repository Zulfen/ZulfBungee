package com.zulfen.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientServer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

@Name("Proxy Server")
@Description("Represents a proxy server from a given name.")
public class ExprProxyServer extends SimpleExpression<ClientServer> {

    private Expression<String> names;

    static {
        Skript.registerExpression(ExprProxyServer.class, ClientServer.class, ExpressionType.SIMPLE, "[(a|the)] (proxy|bungee|bungeecord|velocity) server[s] [(called|named)] %strings%");
    }

    @Override
    protected ClientServer @Nullable [] get(Event event) {
        return Arrays.stream(names.getArray(event))
                .map(name -> ZulfBungeeSpigot.getPlugin().getConnectionManager().getProxyServer(name))
                .flatMap(Optional::stream)
                .toArray(ClientServer[]::new);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends ClientServer> getReturnType() {
        return ClientServer.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "expression proxy server: " + names.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        names = (Expression<String>) expressions[0];
        return true;
    }

}
