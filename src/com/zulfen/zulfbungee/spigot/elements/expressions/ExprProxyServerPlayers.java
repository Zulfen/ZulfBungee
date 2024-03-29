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
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;

import java.util.*;

public class ExprProxyServerPlayers extends SimpleExpression<ClientPlayer> {

    static {
        Skript.registerExpression(ExprProxyServerPlayers.class, ClientPlayer.class, ExpressionType.SIMPLE, "[(all [[of] the]|the)] (bungeecord|bungee|proxy|velocity) players [on [server] %-proxyservers%]");
    }

    private Expression<ClientServer> servers;

    @Override
    protected ClientPlayer @NotNull [] get(@NotNull Event event) {

        List<ClientPlayer> players;

        if (servers != null) {
            ClientServer[] getServers = servers.getArray(event);
            players = ZulfBungeeSpigot.getPlugin().getConnectionManager()
                    .getPlayers(getServers);
        } else {
            players = ZulfBungeeSpigot.getPlugin().getConnectionManager()
                    .getPlayers(new ClientServer[0]);
        }

        return players.toArray(new ClientPlayer[0]);

    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public @NotNull Class<? extends ClientPlayer> getReturnType() {
        return ClientPlayer.class;
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "all bungeecord players";
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        servers = (Expression<ClientServer>) expressions[0];
        return true;
    }
}
