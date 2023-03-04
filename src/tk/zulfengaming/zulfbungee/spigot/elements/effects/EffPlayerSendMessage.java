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
import tk.zulfengaming.zulfbungee.spigot.managers.ConnectionManager;
import tk.zulfengaming.zulfbungee.spigot.socket.SocketConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.PlayerMessage;

import java.util.Optional;

@Name("Send Proxy Player message")
@Description("Sends proxy player(s) a message.")
public class EffPlayerSendMessage extends Effect {

    private Expression<ClientPlayer> players;
    private Expression<String> message;

    static {
        Skript.registerEffect(EffPlayerSendMessage.class, "[(proxy|bungeecord|bungee|velocity) [player]] (message|send|tell) [(proxy|bungeecord|bungee|velocity) [players]] %-proxyplayers% [the message] %string%");
    }

    @Override
    public boolean init(Expression<?> @NotNull [] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        message = (Expression<String>) expressions[1];
        players = (Expression<ClientPlayer>) expressions[0];
        return true;
    }

    @Override
    protected void execute(@NotNull Event event) {

        ConnectionManager connection = ZulfBungeeSpigot.getPlugin().getConnectionManager();
        Optional<ClientServer> asServer = connection.getAsServer();

        asServer.ifPresent(server -> connection.sendDirect(new Packet(PacketTypes.PLAYER_SEND_MESSAGE,
                false, true, new PlayerMessage(server, players.getArray(event), message.getSingle(event)))));

    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "effect message proxy player " + players.toString(event, b) + " with message " + message.toString(event, b);
    }
}
