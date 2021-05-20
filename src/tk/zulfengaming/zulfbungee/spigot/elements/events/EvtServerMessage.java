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

public class EvtServerMessage extends SkriptEvent {

    Literal<String> message;

    static {
        Skript.registerEvent("Server Message", EvtServerMessage.class, EventMessage.class, "server message %string%");

        EventValues.registerEventValue(EventMessage.class, String.class, new Getter<String, EventMessage>() {
            @Override
            public String get(EventMessage eventMessage) {
                return eventMessage.getMessage();
            }
        }, 0);
    }

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.@NotNull ParseResult parseResult) {
        message = (Literal<String>) literals[0];
        return true;
    }

    @Override
    public boolean check(Event event) {

        EventMessage messageEvent = (EventMessage) event;

        return messageEvent.getMessage().equals(message.getSingle());

    }

    @Override
    public String toString(Event event, boolean b) {
        return null;
    }
}
