package tk.zulfengaming.zulfbungee.spigot.elements.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.managers.ProxyServerInfoManager;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.Objects;

@Name("Proxy Server Online")
@Description("Checks if a proxy server is online on the network.")
public class CondIsServerOnline extends PropertyCondition<ProxyServer> {

    static {
        register(CondIsServerOnline.class, "online", "proxyservers");
    }

    @Override
    public boolean check(ProxyServer proxyServer) {
        return ProxyServerInfoManager.contains(proxyServer.getName());
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "online";
    }

}
