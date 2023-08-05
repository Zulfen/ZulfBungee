package com.zulfen.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ExpressionType;
import com.zulfen.zulfbungee.spigot.event.events.EventPlayerServerDisconnect;
import com.zulfen.zulfbungee.spigot.event.events.EventPlayerSwitchServer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class ExprPreviousServer extends EventValueExpression<ClientServer> {

    static {
        Skript.registerExpression(ExprPreviousServer.class, ClientServer.class, ExpressionType.SIMPLE, "(event-previousserver|[the] previous server)");
    }

    public ExprPreviousServer() {
        super(ClientServer.class);
    }

    @Override
    protected ClientServer[] get(@NotNull Event event) {

        if (event instanceof EventPlayerSwitchServer) {
            EventPlayerSwitchServer eventPlayerSwitchServer = (EventPlayerSwitchServer) event;
            return new ClientServer[]{eventPlayerSwitchServer.getFromServer()};
        } else if (event instanceof EventPlayerServerDisconnect) {
            EventPlayerServerDisconnect eventPlayerServerDisconnect = (EventPlayerServerDisconnect) event;
            return new ClientServer[]{eventPlayerServerDisconnect.getLastServer()};
        }

        return null;

    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public boolean init() {
        boolean correctEvent = getParser().isCurrentEvent(EventPlayerSwitchServer.class) || getParser().isCurrentEvent(EventPlayerServerDisconnect.class);
        if (!correctEvent) {
            Skript.error("You can only use this expression in a switch server or connect event!");
        }
        return correctEvent;
    }

    @Override
    public String toString() {
        return "event-previousserver";
    }
}
