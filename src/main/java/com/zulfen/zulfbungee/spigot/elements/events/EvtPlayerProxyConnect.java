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
import com.zulfen.zulfbungee.spigot.event.events.EventPlayerServerConnect;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;

@Name("Proxy Player Connect")
@Description("When a proxy player joins the proxy.")
public class EvtPlayerProxyConnect extends SkriptEvent {

    static {
        Skript.registerEvent("Proxy Player Connect", EvtPlayerProxyConnect.class, EventPlayerServerConnect.class, "(proxy|bungeecord|bungee|velocity) player connect");

        EventValues.registerEventValue(EventPlayerServerConnect.class, ClientPlayer.class, EventPlayerServerConnect::getPlayer);
        EventValues.registerEventValue(EventPlayerServerConnect.class, ClientServer.class, event -> event.getPlayer().getServer().orElse(null));

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
        return "event proxy player connect";
    }
}
