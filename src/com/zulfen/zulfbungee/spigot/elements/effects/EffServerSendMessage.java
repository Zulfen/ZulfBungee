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
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ServerMessage;

import java.util.Optional;

@Name("Proxy Server receive message")
@Description("When a proxy server receives a message.")
public class EffServerSendMessage extends Effect {

    private Expression<ClientServer> servers;
    private Expression<String> message;
    private Expression<String> title;

    static {
        Skript.registerEffect(EffServerSendMessage.class, "[[(proxy|bungeecord|bungee|velocity)] [server]] message [(proxy|bungeecord|bungee|velocity) [server[s]]] %-proxyservers% [the message] %string% (named|called|[with] title[d]) %string%");
    }

    @Override
    protected void execute(@NotNull Event event) {

        ConnectionManager<?> connection = ZulfBungeeSpigot.getPlugin().getConnectionManager();
        Optional<ClientServer> getClientServer  = connection.getAsServer();

        if (getClientServer.isPresent()) {

            ServerMessage messageOut = new ServerMessage(title.getSingle(event), message.getSingle(event), servers.getArray(event),
                    getClientServer.get());

            connection.sendDirect(new Packet(PacketTypes.SERVER_SEND_MESSAGE_EVENT,
                    false, false, messageOut));

        }

    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return "effect server sendDirect message to " + servers.toString(event, b) + " with message " + message + " and title " + title;
    }

    @Override
    public boolean init(Expression<?>[] expressions, int i, @NotNull Kleenean kleenean, SkriptParser.@NotNull ParseResult parseResult) {
        servers = (Expression<ClientServer>) expressions[0];
        message = (Expression<String>) expressions[1];
        title = (Expression<String>) expressions[2];
        return true;
    }
}
