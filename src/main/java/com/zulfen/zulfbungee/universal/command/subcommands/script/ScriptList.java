package com.zulfen.zulfbungee.universal.command.subcommands.script;

import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import com.zulfen.zulfbungee.universal.handlers.CommandHandler;
import com.zulfen.zulfbungee.universal.managers.MainServer;

public class ScriptList<P, T, C> extends CommandHandler<P, T, C> {

    public ScriptList(MainServer<P, T, C> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.script.list", "scripts", "list");
    }

    @Override
    public void handleCommand(ProxyCommandSender<P, T, C> sender, String[] separateArgs) {
        sender.sendPluginMessage("Listing all available scripts...");
        sender.sendPluginMessage(getMainServer().getImpl().getConfig().getScriptPaths().toString());
    }

}
