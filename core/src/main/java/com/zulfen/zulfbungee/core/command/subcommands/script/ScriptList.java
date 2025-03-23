package com.zulfen.zulfbungee.core.command.subcommands.script;

import com.zulfen.zulfbungee.core.command.ProxyCommandSender;
import com.zulfen.zulfbungee.core.handlers.CommandHandler;
import com.zulfen.zulfbungee.core.managers.MainServer;

public class ScriptList<P, T, C> extends CommandHandler<P, T, C> {

    public ScriptList(MainServer<P, T, C> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.script.list", "scripts", "list");
    }

    @Override
    public void handleCommand(ProxyCommandSender sender, String[] separateArgs) {
        sender.sendPluginMessage("Listing all available scripts...");
        sender.sendPluginMessage(getMainServer().getImpl().getConfig().getScriptPaths().toString());
    }

}
