package com.zulfen.zulfbungee.spigot.event.events;

import com.zulfen.zulfbungee.spigot.event.ProxyBukkitEvent;
import org.bukkit.event.HandlerList;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientPlayer;

import java.util.UUID;

public class EventPlayerServerConnect extends ProxyBukkitEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final ClientPlayer player;

    public EventPlayerServerConnect(UUID eventId, ClientPlayer player) {
        super(eventId);
        this.player = player;
    }

    public ClientPlayer getPlayer() {
        return player;
    }

}
