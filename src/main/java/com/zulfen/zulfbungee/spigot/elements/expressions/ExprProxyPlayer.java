package com.zulfen.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Optional;

@Name("Proxy Player")
@Description("Represents a proxy player from a given name.")
public class ExprProxyPlayer extends SimpleExpression<ClientPlayer> {

    private Expression<String> names;

    static {
        Skript.registerExpression(ExprProxyPlayer.class, ClientPlayer.class, ExpressionType.SIMPLE, "[(a|the)] (proxy|bungee|bungeecord|velocity) player[s] [(called|named)] %strings%");
    }

    @Override
    protected ClientPlayer @Nullable [] get(Event event) {
        return Arrays.stream(names.getArray(event))
                .map(name -> ZulfBungeeSpigot.getPlugin()
                        .getConnectionManager()
                        .send(new Packet(PacketTypes.PROXY_PLAYER_UUID, true, true, name)))
                .flatMap(Optional::stream)
                .filter(packet -> packet.getDataArray().length > 0)
                .map(Packet::getDataSingle)
                .map(ClientPlayer.class::cast)
                .toArray(ClientPlayer[]::new);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends ClientPlayer> getReturnType() {
        return ClientPlayer.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "expression proxy player: " + names.toString(event, debug);
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        names = (Expression<String>) expressions[0];
        return true;
    }
}
