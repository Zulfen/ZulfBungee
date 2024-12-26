package com.zulfen.zulfbungee.spigot.event.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;

public class EventPlayerServerConnect extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final ClientPlayer player;

    public EventPlayerServerConnect(ClientPlayer player) {
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

    public ClientPlayer getPlayer() {
        return player;
    }
}
