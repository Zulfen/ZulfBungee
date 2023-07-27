package com.zulfen.zulfbungee.spigot.event.events;

import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ServerMessage;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class EventProxyMessage extends Event {

    private static final HandlerList handlerList = new HandlerList();

    private final ServerMessage message;

    public EventProxyMessage(ServerMessage messageIn) {
        super(true);
        this.message = messageIn;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    public ServerMessage getMessage() {
        return message;
    }

}
