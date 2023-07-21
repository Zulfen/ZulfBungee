package tk.zulfengaming.zulfbungee.universal.command.subcommands.script;

import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.command.util.Constants;
import tk.zulfengaming.zulfbungee.universal.handlers.CommandHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;

public class ScriptList<P, T> extends CommandHandler<P, T> {

    public ScriptList(MainServer<P, T> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.script.list", "scripts", "list");
    }

    @Override
    public void handleCommand(ProxyCommandSender<P, T> sender, String[] separateArgs) {
        sender.sendMessage(Constants.MESSAGE_PREFIX + "Listing all available scripts...");
        sender.sendMessage(Constants.MESSAGE_PREFIX + getMainServer().getPluginInstance().getConfig().getScriptPaths().toString());
    }

}
