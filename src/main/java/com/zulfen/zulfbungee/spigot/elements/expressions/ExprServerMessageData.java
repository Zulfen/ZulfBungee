package com.zulfen.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ExpressionType;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.event.events.EventProxyMessage;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ServerMessage;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class ExprServerMessageData extends EventValueExpression<Object> {

    static {
        Skript.registerExpression(ExprServerMessageData.class, Object.class, ExpressionType.SIMPLE, "(event-data|[the] [message] data)");
    }

    public ExprServerMessageData() {
        super(Object.class);
    }

    @Override
    protected Object[] get(@NotNull Event event) {
        EventProxyMessage eventProxyMessage = (EventProxyMessage) event;
        ServerMessage message = eventProxyMessage.getMessage();
        Object[] objects = ZulfBungeeSpigot.getPlugin().getConnectionManager().threadSafeDeserialize(message.getData());
        if (objects.length == 0) {
            return null;
        } else {
            return objects;
        }
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public boolean init() {
        boolean correctEvent = getParser().isCurrentEvent(EventProxyMessage.class);
        if (!correctEvent) {
            Skript.error("This expression can only be used in a server message event!");
        }
        return correctEvent;
    }
}
