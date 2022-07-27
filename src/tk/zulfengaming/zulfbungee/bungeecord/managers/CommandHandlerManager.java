package tk.zulfengaming.zulfbungee.bungeecord.managers;

import tk.zulfengaming.zulfbungee.bungeecord.command.CheckUpdate;
import tk.zulfengaming.zulfbungee.bungeecord.command.subcommands.script.ScriptReload;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.CommandHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;


public class CommandHandlerManager {

    private final Server server;

    private final HashMap<String, CommandHandler> handlers = new HashMap<>();

    public CommandHandlerManager(Server serverIn) {
        this.server = serverIn;
        addHandler(new ScriptReload(serverIn));
        addHandler(new CheckUpdate(serverIn));
    }

    public Server getMainServer() {
        return server;
    }

    public void addHandler(CommandHandler handlerIn) {
        handlers.put(handlerIn.getMainLabel(), handlerIn);
    }

    public Collection<CommandHandler> getHandlers(){
        return handlers.values();
    }

    public Optional<CommandHandler> getHandler(String mainLabelIn) {
        return Optional.ofNullable(handlers.get(mainLabelIn));
    }
}
