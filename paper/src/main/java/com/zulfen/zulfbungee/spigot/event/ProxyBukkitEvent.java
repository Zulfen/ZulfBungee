package com.zulfen.zulfbungee.spigot.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class ProxyBukkitEvent extends Event {

    private static final HandlerList handlerList = new HandlerList();
    private final UUID eventId;

    public ProxyBukkitEvent(UUID eventId) {
        super(true);
        this.eventId = eventId;
    }

    public UUID getEventId() {
        return eventId;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

}
