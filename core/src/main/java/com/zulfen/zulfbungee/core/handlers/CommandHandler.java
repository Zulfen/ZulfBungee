package com.zulfen.zulfbungee.core.handlers;

import com.zulfen.zulfbungee.core.command.ProxyCommandSender;
import com.zulfen.zulfbungee.core.managers.MainServer;

import java.util.Collection;
import java.util.Collections;

public abstract class CommandHandler<P, T, C> {

    private final String[] labels;

    private final String permission;

    private final MainServer<P, T, C> mainServer;

    public abstract void handleCommand(ProxyCommandSender sender, String[] separateArgs);

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

    public MainServer<P, T, C> getMainServer() {
        return mainServer;
    }

    public CommandHandler(MainServer<P, T, C> mainServerIn, String permissionIn, String... labels) {
        this.labels = labels;
        this.mainServer = mainServerIn;
        this.permission = permissionIn;
    }
}
