package tk.zulfengaming.zulfbungee.universal.command.subcommands.script;

import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.command.util.Constants;
import tk.zulfengaming.zulfbungee.universal.handlers.CommandHandler;
import tk.zulfengaming.zulfbungee.universal.managers.MainServer;

public class ScriptList<P, T> extends CommandHandler<P, T> {

    public ScriptList(MainServer<P, T> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.script.list", "scripts", "list");
    }

    @Override
    public void handleCommand(ProxyCommandSender<P, T> sender, String[] separateArgs) {
        sender.sendPluginMessage("Listing all available scripts...");
        sender.sendPluginMessage(getMainServer().getPluginInstance().getConfig().getScriptPaths().toString());
    }

}
