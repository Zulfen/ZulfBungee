package tk.zulfengaming.zulfbungee.spigot.event.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import tk.zulfengaming.zulfbungee.universal.util.skript.ServerMessage;

public class EventMessage extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final ServerMessage message;

    public EventMessage(ServerMessage messageIn) {
        this.message = messageIn;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public ServerMessage getMessage() {
        return message;
    }

}
