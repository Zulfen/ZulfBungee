package tk.zulfengaming.zulfbungee.universal.command.subcommands;

import tk.zulfengaming.zulfbungee.universal.interfaces.CommandHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;

public class CheckUpdate extends CommandHandler {

    public CheckUpdate(MainServer mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.update.check", "update", "check");
    }

    @Override
    public void handleCommand(ProxyCommandSender sender, String[] separateArgs) {
        sender.sendMessage("Checking for an update...");
        getMainServer().getPluginInstance().checkUpdate(sender, true);
    }
}
