package tk.zulfengaming.zulfbungee.spigot.elements.conditions;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyServer;

@Name("Proxy Server Online")
@Description("Checks if a proxy server is online on the network.")
public class CondIsServerOnline extends PropertyCondition<ProxyServer> {

    static {
        register(CondIsServerOnline.class, "online", "proxyservers");
    }

    @Override
    public boolean check(ProxyServer proxyServer) {
        return ZulfBungeeSpigot.getPlugin().getConnection().proxyServerOnline(proxyServer.getName());
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "online";
    }

}
