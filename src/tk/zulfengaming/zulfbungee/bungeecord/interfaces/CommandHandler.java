package tk.zulfengaming.zulfbungee.bungeecord.interfaces;

import net.md_5.bungee.api.CommandSender;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;

import java.util.Collection;
import java.util.Collections;

public abstract class CommandHandler {

    private final String[] otherLabels;

    private final String permission;
    private final String mainLabel;

    private final Server mainServer;

    public abstract void handleCommand(CommandSender sender, String[] separateArgs);

    public Collection<String> onTab(int index) {
        return Collections.emptyList();
    }

    public String getMainLabel() {
        return mainLabel;
    }

    public String[] getOtherLabels() {
        return otherLabels;
    }

    public String getPermission() {
        return permission;
    }

    public Server getMainServer() {
        return mainServer;
    }

    public CommandHandler(Server serverIn, String permissionIn, String mainLabel, String... otherLabels) {
        this.mainLabel = mainLabel;
        this.otherLabels = otherLabels;
        this.mainServer = serverIn;
        this.permission = permissionIn;
    }
}
