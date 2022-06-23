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
import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerServerDisconnect;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;

@Name("Proxy Player Disconnect")
@Description("When a proxy player disconnects from the proxy.")
public class EvtPlayerProxyDisconnect extends SkriptEvent {

    static {
        Skript.registerEvent("Proxy Player Disconnect", EvtPlayerProxyDisconnect.class, EventPlayerServerDisconnect.class, "(proxy|bungeecord|bungee) player disconnect");

        EventValues.registerEventValue(EventPlayerServerDisconnect.class, ProxyPlayer.class, new Getter<ProxyPlayer, EventPlayerServerDisconnect>() {
            @Override
            public ProxyPlayer get(EventPlayerServerDisconnect eventPlayerServerDisconnect) {
                return eventPlayerServerDisconnect.getPlayer();
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
        return "event proxy player disconnect";
    }
}
