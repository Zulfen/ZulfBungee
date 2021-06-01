package tk.zulfengaming.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;

import java.util.Optional;

public class ExprCurrentServer extends SimpleExpression<String> {

    static {
        Skript.registerExpression(ExprCurrentServer.class, String.class, ExpressionType.SIMPLE, "[the] name of this [script's] server");
    }

    @Override
    protected String[] get(Event event) {

        ClientConnection connection = ZulfBungeeSpigot.getPlugin().getConnection();

        Optional<String> serverName = connection.getServerName();

        return serverName.map(s -> new String[]{s}).orElse(null);

    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Event event, boolean b) {
        return null;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        return true;
    }
}
