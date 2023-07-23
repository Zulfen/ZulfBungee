package tk.zulfengaming.zulfbungee.universal.command.subcommands;

import tk.zulfengaming.zulfbungee.universal.command.util.Constants;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.handlers.CommandHandler;
import tk.zulfengaming.zulfbungee.universal.managers.MainServer;

public class Debug<P, T> extends CommandHandler<P, T> {

    public Debug(MainServer<P, T> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.debug", "debug");
    }

    @Override
    public void handleCommand(ProxyCommandSender<P, T> sender, String[] separateArgs) {
        sender.sendMessage(Constants.MESSAGE_PREFIX + String.format("You are running on %s!", getMainServer().getPluginInstance().platformString()));
    }
}
