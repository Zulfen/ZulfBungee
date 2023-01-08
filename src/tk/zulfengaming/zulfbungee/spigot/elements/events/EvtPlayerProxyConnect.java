package tk.zulfengaming.zulfbungee.spigot.elements.events;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerServerConnect;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;

@Name("Proxy Player Connect")
@Description("When a proxy player joins the proxy.")
public class EvtPlayerProxyConnect extends SkriptEvent {

    static {
        Skript.registerEvent("Proxy Player Connect", EvtPlayerProxyConnect.class, EventPlayerServerConnect.class, "(proxy|bungeecord|bungee|velocity) player connect");

        EventValues.registerEventValue(EventPlayerServerConnect.class, ClientPlayer.class, new Getter<ClientPlayer, EventPlayerServerConnect>() {

            @Override
            public ClientPlayer get(EventPlayerServerConnect eventPlayerServerConnect) {
                return eventPlayerServerConnect.getPlayer();
            }
        }, 0);

        EventValues.registerEventValue(EventPlayerServerConnect.class, ClientServer.class, new Getter<ClientServer, EventPlayerServerConnect>() {
            @Override
            public ClientServer get(EventPlayerServerConnect eventPlayerServerConnect) {
                return eventPlayerServerConnect.getPlayer().getServer();
            }
        }, 0);
    }

    @Override
    public boolean init(Literal<?> @NotNull [] literals, int i, SkriptParser.@NotNull ParseResult parseResult) {
        return true;
    }

    @Override
    public boolean check(Event event) {
        return true;
    }

    @Override
    public String toString(Event event, boolean b) {
        return null;
    }
}
