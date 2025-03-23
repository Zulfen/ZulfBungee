package com.zulfen.zulfbungee.core.command.subcommands;

import com.zulfen.zulfbungee.core.command.ProxyCommandSender;
import com.zulfen.zulfbungee.core.managers.MainServer;
import com.zulfen.zulfbungee.core.handlers.CommandHandler;

public class CheckUpdate<P, T, C> extends CommandHandler<P, T, C> {

    public CheckUpdate(MainServer<P, T, C> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.update.check", "update", "check");
    }

    @Override
    public void handleCommand(ProxyCommandSender sender, String[] separateArgs) {
        sender.sendPluginMessage("Checking for an update...");
        getMainServer().getCheckUpdateTask().checkUpdate(sender, true);
    }

}
