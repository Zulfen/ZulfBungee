package tk.zulfengaming.zulfbungee.spigot.event.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EventMessage extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final String message;

    public EventMessage(String text) {
        this.message = text;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public String getMessage() {
        return message;
    }
}
