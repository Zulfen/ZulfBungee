package tk.zulfengaming.zulfbungee.spigot.event.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

// dummy event to be used exclusively by the network variable section.
// allows us to copy local variables to the section. not sure why the parser works this way, but oh well, it works.
public class EventNetworkVariable extends Event {
    @Override
    public HandlerList getHandlers() {
        throw new IllegalStateException("This is a dummy event, should not be called.");
    }
}
