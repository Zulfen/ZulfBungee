package com.zulfen.zulfbungee.spigot.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import com.zulfen.zulfbungee.spigot.event.events.EventPlayerServerDisconnect;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientPlayer;

@Name("Proxy Player Disconnect")
@Description("When a proxy player disconnects from the proxy.")
public class EvtPlayerProxyDisconnect extends SkriptEvent {

    static {
        Skript.registerEvent("Proxy Player Disconnect", EvtPlayerProxyDisconnect.class, EventPlayerServerDisconnect.class, "(proxy|bungeecord|bungee|velocity) player disconnect");
        EventValues.registerEventValue(EventPlayerServerDisconnect.class, ClientPlayer.class, EventPlayerServerDisconnect::getPlayer);
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
