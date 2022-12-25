package tk.zulfengaming.zulfbungee.spigot.event.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;

public class EventPlayerServerPing extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final ProxyPlayer player;

    private final String address;

    public EventPlayerServerPing(String addressIn, ProxyPlayer player) {
        super(true);
        this.player = player;
        this.address = addressIn;
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

    public String getAddress() {
        return address;
    }

}
