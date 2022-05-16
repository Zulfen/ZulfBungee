package tk.zulfengaming.zulfbungee.spigot.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerSwitchServer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

public class EvtPlayerSwitchServer extends SkriptEvent {

    static {

        Skript.registerEvent("Player Switch Server", EvtPlayerSwitchServer.class, EventPlayerSwitchServer.class, "[(proxy|bungeecord|bungee)] player switch server");

        EventValues.registerEventValue(EventPlayerSwitchServer.class, ProxyPlayer.class, new Getter<ProxyPlayer, EventPlayerSwitchServer>() {
            @Override
            public ProxyPlayer get(EventPlayerSwitchServer eventPlayerSwitchServer) {
                return eventPlayerSwitchServer.getPlayer();
            }
        }, 0);

        EventValues.registerEventValue(EventPlayerSwitchServer.class, ProxyServer.class, new Getter<ProxyServer, EventPlayerSwitchServer>() {
            @Override
            public ProxyServer get(EventPlayerSwitchServer eventPlayerSwitchServer) {
                return eventPlayerSwitchServer.getPlayer().getServer();
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
        return "event player switch server";
    }
}
