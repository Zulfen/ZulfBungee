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
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.ClientUpdateData;

import java.util.Optional;

public class ExprCurrentServer extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprCurrentServer.class, String.class, ExpressionType.SIMPLE, "[the] name of this [script's] server");
    }

    @Override
    protected String @NotNull [] get(@NotNull Event event) {

        ClientConnection connection = ZulfBungeeSpigot.getPlugin().getConnection();

        Optional<ClientUpdateData> clientUpdate = connection.getClientUpdate();

        return clientUpdate.map(update -> new String[]{update.getGivenName()}).orElse(null);

    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
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
