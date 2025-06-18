package com.zulfen.zulfbungee.spigot.event.events;

import com.zulfen.zulfbungee.core.socket.objects.client.skript.ServerMessage;
import com.zulfen.zulfbungee.spigot.event.ProxyBukkitEvent;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class EventProxyMessage extends ProxyBukkitEvent {

    private static final HandlerList handlerList = new HandlerList();

    private final ServerMessage message;

    public EventProxyMessage(UUID eventId, ServerMessage messageIn) {
        super(eventId);
        this.message = messageIn;
    }

    public ServerMessage getMessage() {
        return message;
    }

}
