package com.zulfen.zulfbungee.spigot.event.events;

import com.zulfen.zulfbungee.core.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.spigot.event.ProxyBukkitEvent;
import org.bukkit.event.HandlerList;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientPlayer;

import java.util.UUID;

public class EventPlayerSwitchServer extends ProxyBukkitEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final ClientPlayer player;
    private final ClientServer fromServer;

    public EventPlayerSwitchServer(UUID eventId, ClientServer fromServer, ClientPlayer player) {
        super(eventId);
        this.player = player;
        this.fromServer = fromServer;
    }

    public ClientServer getFromServer() {
        return fromServer;
    }

    public ClientPlayer getPlayer() {
        return player;
    }
}
