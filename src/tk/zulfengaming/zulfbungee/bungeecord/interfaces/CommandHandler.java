package tk.zulfengaming.zulfbungee.bungeecord.interfaces;

import net.md_5.bungee.api.CommandSender;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;

public abstract class CommandHandler {

    private final String[] requiredLabels;
    private String[] suggestedLabels = new String[0];

    private final String permission;

    private final Server mainServer;

    public abstract void handleCommand(CommandSender sender, String[] separateArgs);

    public String[] getSuggestedLabels() {
        return suggestedLabels;
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

    public CommandHandler(Server serverIn, String permissionIn, String[] labelsIn, String[] optionalLabelsIn) {
        this.requiredLabels = labelsIn;
        this.suggestedLabels = optionalLabelsIn;
        this.mainServer = serverIn;
        this.permission = permissionIn;
    }
}
