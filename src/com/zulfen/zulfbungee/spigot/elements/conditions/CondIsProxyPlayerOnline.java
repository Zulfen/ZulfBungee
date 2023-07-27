package com.zulfen.zulfbungee.spigot.elements.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;

import java.util.Optional;

@Name("Proxy Player Online")
@Description("Checks if a proxy player is online on the network.")
public class CondIsProxyPlayerOnline extends Condition {

    private Expression<ClientPlayer> expressionPlayer;

    static {
        Skript.registerCondition(CondIsProxyPlayerOnline.class, "[(proxy|bungeecord|bungee|velocity)] [player] %-proxyplayers% (1¦is|2¦is(n't| not)) online");
    }

    @Override
    public boolean check(Event event) {

        ClientPlayer player = expressionPlayer.getSingle(event);

        Optional<Packet> response = ZulfBungeeSpigot.getPlugin().getConnectionManager()
                .send(new Packet(PacketTypes.PLAYER_ONLINE, true, false, player));

        if (response.isPresent()) {
            Packet packetIn = response.get();
            return (boolean) packetIn.getDataSingle() == isNegated();
        }

        return isNegated();

    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return String.format("proxy player %s online", expressionPlayer.toString(event, b));
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        this.expressionPlayer = (Expression<ClientPlayer>) expressions[0];
        setNegated(parseResult.mark == 1);
        return true;
    }
}
