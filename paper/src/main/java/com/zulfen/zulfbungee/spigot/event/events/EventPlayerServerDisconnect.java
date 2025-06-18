package com.zulfen.zulfbungee.spigot.event.events;

import com.zulfen.zulfbungee.core.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.spigot.event.ProxyBukkitEvent;
import org.bukkit.event.HandlerList;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientPlayer;

import java.util.UUID;

public class EventPlayerServerDisconnect extends ProxyBukkitEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final ClientPlayer player;
    private final ClientServer lastServer;

    public EventPlayerServerDisconnect(UUID eventId, ClientPlayer player, ClientServer lastServer) {
        super(eventId);
        this.player = player;
        this.lastServer = lastServer;
    }

    public ClientPlayer getPlayer() {
        return player;
    }

    public ClientServer getLastServer() {
        return lastServer;
    }

}
