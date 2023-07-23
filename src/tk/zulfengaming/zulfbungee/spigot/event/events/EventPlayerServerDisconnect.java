package tk.zulfengaming.zulfbungee.spigot.event.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;

public class EventPlayerServerDisconnect extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final ClientPlayer player;
    private final ClientServer lastServer;

    public EventPlayerServerDisconnect(ClientPlayer player, ClientServer lastServer) {
        super(true);
        this.player = player;
        this.lastServer = lastServer;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public ClientPlayer getPlayer() {
        return player;
    }

    public ClientServer getLastServer() {
        return lastServer;
    }

}
