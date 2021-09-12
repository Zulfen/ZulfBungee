package tk.zulfengaming.zulfbungee.spigot.elements.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import tk.zulfengaming.zulfbungee.spigot.handlers.ClientInfoManager;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

public class CondIsServerOnline extends Condition {

    private Expression<ProxyServer> server;

    static {
        Skript.registerCondition(CondIsServerOnline.class, "%-proxyserver% (1¦is|2¦is(n't| not)) online");
    }

    @Override
    public boolean check(Event event) {

        return ClientInfoManager.getServers().contains(server.getSingle(event));

    }

    @Override
    public String toString(Event event, boolean b) {
        return "condition proxy server " + server.toString(event, b) + " online";
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        server = (Expression<ProxyServer>) expressions[0];
        return true;
    }
}
