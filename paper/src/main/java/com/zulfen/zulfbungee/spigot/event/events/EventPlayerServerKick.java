package com.zulfen.zulfbungee.spigot.event.events;

import com.zulfen.zulfbungee.spigot.event.ProxyBukkitEvent;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientPlayer;

import java.util.UUID;

public class EventPlayerServerKick extends ProxyBukkitEvent {

    private final ClientPlayer player;

    private final String reason;

    public EventPlayerServerKick(UUID eventId, String reason, ClientPlayer player) {
        super(eventId);
        this.player = player;
        this.reason = reason;
    }

    public ClientPlayer getPlayer() {
        return player;
    }

    public String getReason() {
        return reason;
    }

}
