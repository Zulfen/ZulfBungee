package com.zulfen.zulfbungee.universal.command.subcommands;

import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.universal.handlers.CommandHandler;

public class CheckUpdate<P, T, C> extends CommandHandler<P, T, C> {

    public CheckUpdate(MainServer<P, T, C> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.update.check", "update", "check");
    }

    @Override
    public void handleCommand(ProxyCommandSender<P, T, C> sender, String[] separateArgs) {
        sender.sendPluginMessage("Checking for an update...");
        getMainServer().getCheckUpdateTask().checkUpdate(sender, true);
    }

}
