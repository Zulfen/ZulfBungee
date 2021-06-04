package tk.zulfengaming.zulfbungee.spigot.event.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;

public class EventPlayerServerDisconnect extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final ProxyPlayer player;

    public EventPlayerServerDisconnect(ProxyPlayer player) {
        this.player = player;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public ProxyPlayer getPlayer() {
        return player;
    }
}
