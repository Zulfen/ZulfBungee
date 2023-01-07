package tk.zulfengaming.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class ExprProxyServer extends SimpleExpression<ClientServer> {

    private Expression<String> serverNames;

    static {
        Skript.registerExpression(ExprProxyServer.class, ClientServer.class, ExpressionType.SIMPLE, "[(proxy|bungeecord|bungee|velocity)] server[s] [(named|called)] %strings%]");
    }

    @Override
    protected ClientServer @NotNull [] get(@NotNull Event event) {

        return Stream.of(serverNames.getArray(event))
                .map(s -> ZulfBungeeSpigot.getPlugin().getConnection().getProxyServer(s))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toArray(ClientServer[]::new);

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
        return "proxy server(s): " + Arrays.toString(serverNames.getArray(event));
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        serverNames = (Expression<String>) expressions[0];
        return true;
    }
}
