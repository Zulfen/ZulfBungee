package tk.zulfengaming.zulfbungee.spigot.elements.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;

import java.util.UUID;

public class ExprProxyPlayerUUID extends SimplePropertyExpression<ClientPlayer, String> {

    static {
        register(ExprProxyPlayerUUID.class, String.class, "UUID", "proxyplayers");
    }

    @Override
    protected @NotNull String getPropertyName() {
        return "proxy player uuid";
    }

    @Override
    public String convert(ClientPlayer player) {
        return player.getUuid().toString();
    }

    @Override
    public @NotNull Class<? extends String> getReturnType() {
        return String.class;
    }
}
