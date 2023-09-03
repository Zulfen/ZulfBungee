package com.zulfen.zulfbungee.universal.command.subcommands.script;

import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import com.zulfen.zulfbungee.universal.handlers.CommandHandler;
import com.zulfen.zulfbungee.universal.managers.MainServer;

public class ScriptList<P, T> extends CommandHandler<P, T> {

    public ScriptList(MainServer<P, T> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.script.list", "scripts", "list");
    }

    @Override
    public void handleCommand(ProxyCommandSender<P, T> sender, String[] separateArgs) {
        sender.sendPluginMessage("Listing all available scripts...");
        sender.sendPluginMessage(getMainServer().getImpl().getConfig().getScriptPaths().toString());
    }

}
