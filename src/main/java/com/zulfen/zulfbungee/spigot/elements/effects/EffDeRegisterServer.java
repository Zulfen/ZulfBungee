package com.zulfen.zulfbungee.spigot.elements.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

public class EffDeRegisterServer extends Effect {

    private Expression<String> serverName;

    static {
        Skript.registerEffect(EffDeRegisterServer.class, "(de|un)register [a] server [from the proxy] [(named|called)] %string%");
    }

    @Override
    protected void execute(@NotNull Event event) {

        String serverNameOut = serverName.getSingle(event);

        ZulfBungeeSpigot.getPlugin().getConnectionManager().sendDirect(new Packet(PacketTypes.DEREGISTER_SERVER, false, true,
                serverNameOut));

    }

    @Override
    public String toString(Event e, boolean debug) {
        return "effect deregister server";
    }


    @Override
    public boolean init(Expression<?> @NotNull [] exprs, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        serverName = (Expression<String>) exprs[0];
        return true;
    }

}
