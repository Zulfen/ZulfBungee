package tk.zulfengaming.zulfbungee.spigot.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerServerKick;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;

public class EvtPlayerServerKick extends SkriptEvent {

    static {
        Skript.registerEvent("Player Proxy Kick", EvtPlayerServerKick.class, EventPlayerServerKick.class, "(proxy|bungeecord|bungee) player kick");

        EventValues.registerEventValue(EventPlayerServerKick.class, ProxyPlayer.class, new Getter<ProxyPlayer, EventPlayerServerKick>() {
            @Override
            public ProxyPlayer get(EventPlayerServerKick eventPlayerServerKick) {
                return eventPlayerServerKick.getPlayer();
            }
        }, 0);

        EventValues.registerEventValue(EventPlayerServerKick.class, String.class, new Getter<String, EventPlayerServerKick>() {
            @Override
            public String get(EventPlayerServerKick eventPlayerServerKick) {
                return String.join(" ", eventPlayerServerKick.getReason());
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
