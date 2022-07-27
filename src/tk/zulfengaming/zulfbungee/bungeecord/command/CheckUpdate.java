package tk.zulfengaming.zulfbungee.bungeecord.command;

import net.md_5.bungee.api.CommandSender;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.CommandHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.util.MessageUtils;

public class CheckUpdate extends CommandHandler {

    public CheckUpdate(Server serverIn) {
        super(serverIn, "zulfen.bungee.admin.update.check", "update", "check");
    }

    @Override
    public void handleCommand(CommandSender sender, String[] separateArgs) {
        MessageUtils.sendMessage(sender, "Checking for an update...");
        getMainServer().getPluginInstance().checkUpdate(sender, true);
    }
}
