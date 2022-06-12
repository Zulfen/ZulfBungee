package tk.zulfengaming.zulfbungee.spigot.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.event.events.EventMessage;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

public class EvtServerMessage extends SkriptEvent {

    private Literal<String> title;

    static {
        Skript.registerEvent("Server Message", EvtServerMessage.class, EventMessage.class, "[(bungeecord|bungee|proxy)] server message [(titled|called)] %string%");

        EventValues.registerEventValue(EventMessage.class, String.class, new Getter<String, EventMessage>() {
            @Override
            public String get(EventMessage eventMessage) {
                return eventMessage.getMessage().getText();
            }
        }, 0);

        EventValues.registerEventValue(EventMessage.class, ProxyServer.class, new Getter<ProxyServer, EventMessage>() {
            @Override
            public ProxyServer get(EventMessage eventMessage) {
                return eventMessage.getMessage().getFrom();
            }
        }, 0);
    }

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.@NotNull ParseResult parseResult) {
        title = (Literal<String>) literals[0];
        return true;
    }

    @Override
    public boolean check(Event event) {

        EventMessage messageEvent = (EventMessage) event;

        return messageEvent.getMessage().getTitle().equals(title.getSingle());

    }

    @Override
    public String toString(Event event, boolean b) {
        return "server message event with title " + title.toString(event, b);
    }
}
