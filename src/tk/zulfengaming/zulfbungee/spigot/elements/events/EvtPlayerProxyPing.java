package tk.zulfengaming.zulfbungee.spigot.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerServerKick;
import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerServerPing;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;

@Name("Proxy Player Ping")
@Description("When a proxy player pings the proxy from the server menu.")
public class EvtPlayerProxyPing extends SkriptEvent {

    static {
        Skript.registerEvent("Proxy Player Ping", EvtPlayerProxyPing.class, EventPlayerServerPing.class, "(proxy|bungeecord|bungee) player kick");

        EventValues.registerEventValue(EventPlayerServerPing.class, ProxyPlayer.class, new Getter<ProxyPlayer, EventPlayerServerPing>() {
            @Override
            public ProxyPlayer get(EventPlayerServerPing eventPlayerServerPing) {
                return eventPlayerServerPing.getPlayer();
            }
        }, 0);

        EventValues.registerEventValue(EventPlayerServerPing.class, String.class, new Getter<String, EventPlayerServerPing>() {
            @Override
            public String get(EventPlayerServerPing eventPlayerServerPing) {
                return eventPlayerServerPing.getAddress();
            }
        }, 0);
    }

    @Override
    public boolean init(Literal<?> @NotNull [] literals, int i, SkriptParser.@NotNull ParseResult parseResult) {
        return true;
    }

    @Override
    public boolean check(@NotNull Event event) {
        return true;
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "event proxy player ping";
    }
}
