package com.zulfen.zulfbungee.spigot.event;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import org.bukkit.event.Event;

public abstract class ProxySkriptEvent extends SkriptEvent {

    @Override
    public boolean init(Literal<?>[] literals, int i, SkriptParser.ParseResult parseResult) {
        return true;
    }

    // for most events, we don't want to cause duplicates and only have one server actually run the code.
    // this functionality will be commented out for now, but might end up using later.
    @Override
    public boolean check(Event event) {
        /*if (event instanceof ProxyBukkitEvent proxyEvent) {
            Boolean result = ZulfBungeeSpigot.getPlugin()
                    .getConnectionManager()
                    .send(new ProxyEventResponse(proxyEvent.getEventId()))
                    .map(packet -> !packet.getDataSingle(Boolean.class))
                    .orElse(false);
            ZulfBungeeSpigot.getPlugin().error(result.toString());
            return result;

        }
        return false;*/
        return true;
    }

}
