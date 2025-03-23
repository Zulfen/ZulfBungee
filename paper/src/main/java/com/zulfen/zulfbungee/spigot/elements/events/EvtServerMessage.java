package com.zulfen.zulfbungee.spigot.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.core.socket.objects.client.skript.ServerMessage;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import com.zulfen.zulfbungee.spigot.event.events.EventProxyMessage;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientServer;

@Name("Server Message")
@Description("Called when this server receives a message.")
public class EvtServerMessage extends SkriptEvent {

    private Literal<String> title;

    static {
        Skript.registerEvent("Server Message", EvtServerMessage.class, EventProxyMessage.class, "[(bungeecord|bungee|proxy|velocity)] server message [(titled|called)] %string%");

        EventValues.registerEventValue(EventProxyMessage.class, Object[].class, event -> {
                ServerMessage serverMessage = event.getMessage();
                return ZulfBungeeSpigot.getPlugin().getConnectionManager().threadSafeDeserialize(serverMessage.getData());
        });

        EventValues.registerEventValue(EventProxyMessage.class, ClientServer.class, event -> event.getMessage().getFrom());

    }

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.@NotNull ParseResult parseResult) {
        title = (Literal<String>) literals[0];
        return true;
    }

    @Override
    public boolean check(Event event) {
        EventProxyMessage messageEvent = (EventProxyMessage) event;
        return messageEvent.getMessage().getTitle().equals(title.getSingle());
    }

    @Override
    public String toString(Event event, boolean b) {
        return "server message event with title " + title.toString(event, b);
    }

}
