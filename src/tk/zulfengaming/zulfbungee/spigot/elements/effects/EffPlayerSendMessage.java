package tk.zulfengaming.zulfbungee.spigot.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.utilclasses.skript.ProxyPlayer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EffPlayerSendMessage extends Effect {

    private Expression<ProxyPlayer> players;
    private Expression<String> message;

    static {
        Skript.registerEffect(EffPlayerSendMessage.class, "message (proxy|network|bungeecord) player %-proxyplayers% [the message] %string%");
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        message = (Expression<String>) expressions[1];
        players = (Expression<ProxyPlayer>) expressions[0];
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {

        ClientConnection connection = ZulfBungeeSpigot.getPlugin().getConnection();

        ProxyPlayer[] playersArray = players.getArray(event);

        List<String> data = Stream.of(playersArray)
                .map(ProxyPlayer::getName)
                .collect(Collectors.toList());

        data.add(message.getSingle(event));

        connection.send_direct(new Packet(PacketTypes.PLAYER_SEND_MESSAGE,
                        false, false, data.toArray(new String[0])));
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return null;
    }
}
