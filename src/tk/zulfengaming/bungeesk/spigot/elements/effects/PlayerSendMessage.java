package tk.zulfengaming.bungeesk.spigot.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot;
import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PlayerSendMessage extends Effect {

    private Expression<OfflinePlayer> players;
    private Expression<String> message;

    static {
        Skript.registerEffect(PlayerSendMessage.class, "message bungeecord player %offlineplayers% [the message] %string%");
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        message = (Expression<String>) expressions[1];
        players = (Expression<OfflinePlayer>) expressions[0];
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {

        ClientConnection connection = BungeeSkSpigot.getPlugin().getConnection();

        OfflinePlayer[] offlinePlayers = players.getArray(event);

        List<String> data = Stream.of(offlinePlayers)
                .map(OfflinePlayer::getUniqueId)
                .map(UUID::toString)
                .collect(Collectors.toList());

        data.add(message.getSingle(event));

        connection.send_direct(new Packet(Bukkit.getServer().getServerName(), PacketTypes.PLAYER_SEND_MESSAGE,
                        false, false, data.toArray(new String[0])));
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return null;
    }
}
