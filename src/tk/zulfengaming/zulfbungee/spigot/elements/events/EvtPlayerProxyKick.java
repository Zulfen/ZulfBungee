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
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyPlayer;

@Name("Proxy Player Kick")
@Description("When a proxy player gets kicked from the proxy. (e.g. a ban)")
public class EvtPlayerProxyKick extends SkriptEvent {

    static {
        Skript.registerEvent("Proxy Player Kick", EvtPlayerProxyKick.class, EventPlayerServerKick.class, "(proxy|bungeecord|bungee) player kick");

        EventValues.registerEventValue(EventPlayerServerKick.class, ProxyPlayer.class, new Getter<ProxyPlayer, EventPlayerServerKick>() {
            @Override
            public ProxyPlayer get(EventPlayerServerKick eventPlayerServerKick) {
                return eventPlayerServerKick.getPlayer();
            }
        }, 0);

        EventValues.registerEventValue(EventPlayerServerKick.class, String.class, new Getter<String, EventPlayerServerKick>() {
            @Override
            public String get(EventPlayerServerKick eventPlayerServerKick) {
                return eventPlayerServerKick.getReason();
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
        return "event proxy player kick";
    }
}
