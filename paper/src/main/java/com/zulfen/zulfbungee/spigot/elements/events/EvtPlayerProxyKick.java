package com.zulfen.zulfbungee.spigot.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.registrations.EventValues;
import com.zulfen.zulfbungee.spigot.event.ProxySkriptEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import com.zulfen.zulfbungee.spigot.event.events.EventPlayerServerKick;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientPlayer;

@Name("Proxy Player Kick")
@Description("When a proxy player gets kicked from the proxy. (e.g. a ban)")
public class EvtPlayerProxyKick extends ProxySkriptEvent {

    static {
        Skript.registerEvent("Proxy Player Kick", EvtPlayerProxyKick.class, EventPlayerServerKick.class, "(proxy|bungeecord|bungee|velocity) player kick");

        EventValues.registerEventValue(EventPlayerServerKick.class, ClientPlayer.class, EventPlayerServerKick::getPlayer);
        EventValues.registerEventValue(EventPlayerServerKick.class, String.class, EventPlayerServerKick::getReason);

    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "event proxy player kick";
    }

}
