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
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.skript.ProxyPlayerDataContainer;

@Name("Send Proxy Player message")
@Description("Sends proxy player(s) a message.")
public class EffPlayerSendMessage extends Effect {

    private Expression<ProxyPlayer> players;
    private Expression<String> message;

    static {
        Skript.registerEffect(EffPlayerSendMessage.class, "[(proxy|bungeecord|bungee)] (message|send|tell) %-proxyplayers% [the message] %string%");
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

        connection.send(new Packet(PacketTypes.PLAYER_SEND_MESSAGE,
                        false, false, new ProxyPlayerDataContainer(message.getSingle(event), players.getArray(event))));
    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "effect message proxy player " + players.toString(event, b) + " with message " + message.toString(event, b);
    }
}
