package tk.zulfengaming.zulfbungee.spigot.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerDisconnect;
import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerSwitchServer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;

public class EvtProxyPlayerDisconnect extends SkriptEvent {

    static {
        Skript.registerEvent("Player Bungee Disconnect", EvtProxyPlayerDisconnect.class, EventPlayerSwitchServer.class, "player disconnect from proxy");

        EventValues.registerEventValue(EventPlayerDisconnect.class, ProxyPlayer.class, new Getter<ProxyPlayer, EventPlayerDisconnect>() {
            @Override
            public ProxyPlayer get(EventPlayerDisconnect eventPlayerDisconnect) {
                return eventPlayerDisconnect.getPlayer();
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
