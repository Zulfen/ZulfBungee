package tk.zulfengaming.zulfbungee.spigot.elements.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;

@Name("Proxy Server Online")
@Description("Checks if a proxy server is online on the network.")
public class CondIsServerOnline extends Condition {

    private Expression<ClientServer> serverExpression;

    static {
        Skript.registerCondition(CondIsServerOnline.class, "[(proxy|bungeecord|bungee|velocity)] [server] %-proxyserver% (1¦is|2¦is(n't| not)) online");
    }

    @Override
    public boolean check(Event event) {

        ClientServer server = serverExpression.getSingle(event);

        if (server != null) {
            return ZulfBungeeSpigot.getPlugin().getConnection().proxyServerOnline(server.getName()) == isNegated();
        }

        return !isNegated();

    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return String.format("proxy server %s online", serverExpression.toString(event, b));
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        this.serverExpression = (Expression<ClientServer>) expressions[0];
        setNegated(parseResult.mark == 1);
        return true;
    }
}
