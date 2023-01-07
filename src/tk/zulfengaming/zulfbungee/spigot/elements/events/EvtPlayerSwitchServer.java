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
import tk.zulfengaming.zulfbungee.spigot.event.events.EventPlayerSwitchServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;

@Name("Proxy Player Switch Server")
@Description("When a proxy player switches to another server.")
public class EvtPlayerSwitchServer extends SkriptEvent {

    static {

        Skript.registerEvent("Proxy Player Switch Server", EvtPlayerSwitchServer.class, EventPlayerSwitchServer.class, "[(proxy|bungeecord|bungee|velocity)] player switch server");

        EventValues.registerEventValue(EventPlayerSwitchServer.class, ClientPlayer.class, new Getter<ClientPlayer, EventPlayerSwitchServer>() {
            @Override
            public ClientPlayer get(EventPlayerSwitchServer eventPlayerSwitchServer) {
                return eventPlayerSwitchServer.getPlayer();
            }
        }, 0);

        EventValues.registerEventValue(EventPlayerSwitchServer.class, ClientServer.class, new Getter<ClientServer, EventPlayerSwitchServer>() {
            @Override
            public ClientServer get(EventPlayerSwitchServer eventPlayerSwitchServer) {
                return eventPlayerSwitchServer.getPlayer().getServer();
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
        return "event player switch server";
    }
}
