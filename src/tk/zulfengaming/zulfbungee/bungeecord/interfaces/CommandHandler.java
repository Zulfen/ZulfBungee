package tk.zulfengaming.zulfbungee.bungeecord.interfaces;

import net.md_5.bungee.api.CommandSender;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;

public abstract class CommandHandler {

    private final String[] labels;

    private final String permission;

    private final Server mainServer;

    public abstract void handleCommand(CommandSender sender, String[] separateArgs);

    public String[] getLabels() {
        return labels;
    }

    public String getPermission() {
        return permission;
    }

    public Server getMainServer() {
        return mainServer;
    }

    public CommandHandler(Server serverIn, String permissionIn, String... labelsIn) {
        this.labels = labelsIn;
        this.mainServer = serverIn;
        this.permission = permissionIn;
    }
}
