package tk.zulfengaming.zulfbungee.universal.interfaces;

import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;

import java.util.Collection;
import java.util.Collections;

public abstract class CommandHandler<P> {

    private final String[] labels;

    private final String permission;

    private final MainServer<P> mainServer;

    public abstract void handleCommand(ProxyCommandSender<P> sender, String[] separateArgs);

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

    public MainServer<P> getMainServer() {
        return mainServer;
    }

    public CommandHandler(MainServer<P> mainServerIn, String permissionIn, String... labels) {
        this.labels = labels;
        this.mainServer = mainServerIn;
        this.permission = permissionIn;
    }
}
