package tk.zulfengaming.zulfbungee.spigot.event.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyPlayer;

public class EventPlayerServerKick extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final ProxyPlayer player;

    private final String reason;

    public EventPlayerServerKick(String reason, ProxyPlayer player) {
        super(true);
        this.player = player;
        this.reason = reason;
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

    public String getReason() {
        return reason;
    }
}
