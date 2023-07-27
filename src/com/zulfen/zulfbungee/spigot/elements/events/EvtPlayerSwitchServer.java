package com.zulfen.zulfbungee.spigot.elements.events;

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
import com.zulfen.zulfbungee.spigot.event.events.EventPlayerSwitchServer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;

import java.util.Optional;

@Name("Proxy Player Switch Server")
@Description("When a proxy player switches to another server.")
public class EvtPlayerSwitchServer extends SkriptEvent {

    static {

        Skript.registerEvent("Proxy Player Switch Server", EvtPlayerSwitchServer.class, EventPlayerSwitchServer.class, "[(proxy|bungeecord|bungee|velocity)] player switch server");

        EventValues.registerEventValue(EventPlayerSwitchServer.class, ClientPlayer.class, new Getter<ClientPlayer, EventPlayerSwitchServer>() {
            @Override
            public ClientPlayer get(EventPlayerSwitchServer eventPlayerSwitchServer) {
                return eventPlayerSwitchServer.getPlayer();
            }
        }, 0);

        EventValues.registerEventValue(EventPlayerSwitchServer.class, ClientServer.class, new Getter<ClientServer, EventPlayerSwitchServer>() {
            @Override
            public ClientServer get(EventPlayerSwitchServer eventPlayerSwitchServer) {
                Optional<ClientServer> getServer = eventPlayerSwitchServer.getPlayer().getServer();
                return getServer.orElse(null);
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
        return "event player switch server";
    }
}
