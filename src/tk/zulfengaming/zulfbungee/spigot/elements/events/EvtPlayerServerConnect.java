package tk.zulfengaming.zulfbungee.spigot.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerServerConnect;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

public class EvtPlayerServerConnect extends SkriptEvent {

    static {
        Skript.registerEvent("Player Proxy Connect", EvtPlayerServerConnect.class, EventPlayerServerConnect.class, "(proxy|bungeecord|bungee) player connect");

        EventValues.registerEventValue(EventPlayerServerConnect.class, ProxyPlayer.class, new Getter<ProxyPlayer, EventPlayerServerConnect>() {

            @Override
            public ProxyPlayer get(EventPlayerServerConnect eventPlayerServerConnect) {
                return eventPlayerServerConnect.getPlayer();
            }
        }, 0);

        EventValues.registerEventValue(EventPlayerServerConnect.class, ProxyServer.class, new Getter<ProxyServer, EventPlayerServerConnect>() {
            @Override
            public ProxyServer get(EventPlayerServerConnect eventPlayerServerConnect) {
                return eventPlayerServerConnect.getPlayer().getServer();
            }
        }, 0);
    }

    @Override
    public boolean init(Literal<?> @NotNull [] literals, int i, SkriptParser.@NotNull ParseResult parseResult) {
        return true;
    }

    @Override
    public boolean check(Event event) {
        return true;
    }

    @Override
    public String toString(Event event, boolean b) {
        return null;
    }
}
