package tk.zulfengaming.zulfbungee.bungeecord.interfaces;

import net.md_5.bungee.api.CommandSender;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class CommandHandler {

    private final String[] requiredLabels;

    private final String permission;

    private final Server mainServer;

    public abstract void handleCommand(CommandSender sender, String[] separateArgs);

    public Collection<String> onTab(int index) {
        return Collections.emptyList();
    }

    public String[] getRequiredLabels() {
        return requiredLabels;
    }

    public String getPermission() {
        return permission;
    }

    public Server getMainServer() {
        return mainServer;
    }

    public CommandHandler(Server serverIn, String permissionIn, String... labelsIn) {
        this.requiredLabels = labelsIn;
        this.mainServer = serverIn;
        this.permission = permissionIn;
    }
}
