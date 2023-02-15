package tk.zulfengaming.zulfbungee.spigot.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;

@Name("Kick Proxy Player")
@Description("Kicks a proxy player from the network.")
public class EffProxyPlayerKick extends Effect {

    private Expression<String> reason;
    private Expression<ClientPlayer> players;

    static {
        Skript.registerEffect(EffProxyPlayerKick.class, "kick [(proxy|bungeecord|bungee|velocity) [player]] %-proxyplayers% [(by reason of|because [of]|on account of|due to|with [reason]) %-string%]");
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        reason = (Expression<String>) expressions[1];
        players = (Expression<ClientPlayer>) expressions[0];
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {
        ZulfBungeeSpigot.getPlugin().getConnection().sendDirect(new Packet(PacketTypes.KICK_PLAYER,
                            false, true, new ClientPlayerDataContainer(reason.getSingle(event), players.getArray(event))));
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "effect kick proxy player " + players.toString(event, b);
    }
}
