package tk.zulfengaming.zulfbungee.bungeecord.interfaces;

import net.md_5.bungee.api.CommandSender;
import tk.zulfengaming.zulfbungee.bungeecord.socket.MainServer;

import java.util.Collection;
import java.util.Collections;

public abstract class CommandHandler {

    private final String[] labels;

    private final String permission;

    private final MainServer mainServer;

    public abstract void handleCommand(CommandSender sender, String[] separateArgs);

    public Collection<String> onTab(int index) {
        return Collections.emptyList();
    }

    public String getMainLabel() {
        return labels[0];
    }

    public String[] getRequiredLabels() {
        return labels;
    }

    public String getBasePermission() {
        return permission;
    }

    public MainServer getMainServer() {
        return mainServer;
    }

    public CommandHandler(MainServer mainServerIn, String permissionIn, String... labels) {
        this.labels = labels;
        this.mainServer = mainServerIn;
        this.permission = permissionIn;
    }
}
