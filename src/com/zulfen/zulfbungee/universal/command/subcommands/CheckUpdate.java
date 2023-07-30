package com.zulfen.zulfbungee.universal.command.subcommands;

import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.universal.handlers.CommandHandler;

public class CheckUpdate<P, T> extends CommandHandler<P, T> {

    public CheckUpdate(MainServer<P, T> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.update.check", "update", "check");
    }

    @Override
    public void handleCommand(ProxyCommandSender<P, T> sender, String[] separateArgs) {
        sender.sendPluginMessage("Checking for an update...");
        getMainServer().getPluginInstance().getUpdater().checkUpdate(sender, true);
    }

}
