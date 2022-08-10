package tk.zulfengaming.zulfbungee.spigot.event.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;

public class EventPlayerSwitchServer extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final ProxyPlayer player;

    public EventPlayerSwitchServer(ProxyPlayer player) {
        super(true);
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
