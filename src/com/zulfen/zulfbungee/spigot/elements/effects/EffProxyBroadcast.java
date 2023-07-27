package com.zulfen.zulfbungee.spigot.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.managers.ConnectionManager;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ClientServerDataContainer;

@Name("Broadcast")
@Description("Broadcasts a message.")
public class EffProxyBroadcast extends Effect {

    private Expression<ClientServer> servers;
    private Expression<String> message;

    static {
        Skript.registerEffect(EffProxyBroadcast.class, "(proxy|bungeecord|bungee|velocity) broadcast [the [message]] %string% [(on|to) %-proxyservers%]");
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        message = (Expression<String>) expressions[0];
        servers = (Expression<ClientServer>) expressions[1];
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {

        ConnectionManager connection = ZulfBungeeSpigot.getPlugin().getConnectionManager();

        if (servers != null) {
            connection.sendDirect(new Packet(PacketTypes.BROADCAST_MESSAGE, false, true, new ClientServerDataContainer(servers.getArray(event), message.getSingle(event))));
        } else {
            connection.sendDirect(new Packet(PacketTypes.BROADCAST_MESSAGE, false, true,  new ClientServerDataContainer(new ClientServer[0], message.getSingle(event))));
        }

    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "effect broadcast " + message.toString(event, b) + " to servers " + servers.toString(event, b);
    }
}
