package com.zulfen.zulfbungee.spigot.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

@Name("Kick Proxy Player")
@Description("Kicks a proxy player from the network.")
public class EffProxyPlayerKick extends Effect {

    private Expression<String> reason;
    private Expression<ClientPlayer> players;

    static {
        Skript.registerEffect(EffProxyPlayerKick.class, "[(proxy|bungeecord|bungee|velocity) [player]] kick [(proxy|bungeecord|bungee|velocity) [player]] %-proxyplayers% [(by reason of|because [of]|on account of|due to|with [reason]) %-string%]");
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        reason = (Expression<String>) expressions[1];
        players = (Expression<ClientPlayer>) expressions[0];
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        ZulfBungeeSpigot.getPlugin().getConnectionManager().sendDirect(new Packet(PacketTypes.KICK_PLAYER,
                            false, true, new ClientPlayerDataContainer(reason.getSingle(event), players.getArray(event))));
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "effect kick proxy player " + players.toString(event, b);
    }
}
