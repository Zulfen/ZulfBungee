package tk.zulfengaming.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ExpressionType;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerServerDisconnect;
import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerSwitchServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;

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
        return getParser().isCurrentEvent(EventPlayerSwitchServer.class) || getParser().isCurrentEvent(EventPlayerServerDisconnect.class);
    }

    @Override
    public String toString() {
        return "event-previousserver";
    }
}
