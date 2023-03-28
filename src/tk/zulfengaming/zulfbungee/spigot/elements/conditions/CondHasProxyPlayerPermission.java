package tk.zulfengaming.zulfbungee.spigot.elements.conditions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;

import java.util.Optional;

@Name("Proxy Player Has Permission")
@Description("Checks if a proxy player has a permission. Keep in mind, this asks the proxy if the player has the permission, so make sure there is a permission manager on the proxy!")
public class CondHasProxyPlayerPermission extends Condition {

    private Expression<ClientPlayer> expressionPlayer;
    private Expression<String> permissions;

    static {
        Skript.registerCondition(CondHasProxyPlayerPermission.class, "[(proxy|bungeecord|bungee|velocity) [player]] %-proxyplayers% (has|have) [the] permission[s] %strings%",
                "[(proxy|bungeecord|bungee|velocity) [player]] %-proxyplayers% (doesn't|does not|do not|don't) have [the] permission[s] %strings%");
    }

    @Override
    public boolean check(@NotNull Event event) {

        Optional<Packet> response = ZulfBungeeSpigot.getPlugin().getConnectionManager()
                .send(new Packet(PacketTypes.PROXY_PLAYER_PERMISSION, true, false, new ClientPlayerDataContainer(permissions.getArray(event), expressionPlayer.getSingle(event))));

        if (response.isPresent()) {
            Packet packetIn = response.get();
            ZulfBungeeSpigot.getPlugin().warning(String.format("result: %s, isNegated: %s", packetIn.getDataSingle(), isNegated()));
            return (boolean) packetIn.getDataSingle() != isNegated();
        }

        return isNegated();

    }

    @Override
    public @NotNull String toString(Event event, boolean b) {
        return String.format("proxy player %s has permission", expressionPlayer.toString(event, b));
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, @NotNull Kleenean kleenean, SkriptParser.ParseResult parseResult) {
        this.expressionPlayer = (Expression<ClientPlayer>) expressions[0];
        this.permissions = (Expression<String>) expressions[1];
        setNegated(matchedPattern == 1);
        return true;
    }
}
