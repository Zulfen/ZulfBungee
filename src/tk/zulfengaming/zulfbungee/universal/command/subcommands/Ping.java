package tk.zulfengaming.zulfbungee.universal.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.zulfengaming.zulfbungee.universal.command.Constants;
import tk.zulfengaming.zulfbungee.universal.interfaces.CommandHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyPlayer;

public class Ping extends CommandHandler {

    public Ping(MainServer mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.ping", "ping");
    }

    @Override
    public void handleCommand(ProxyCommandSender sender, String[] separateArgs) {

        String serverName = ((ProxyPlayer) sender).getServer().getName();
        long ping = getMainServer().getConnectionFromName(serverName).getPing();

        sender.sendMessage(Constants.MESSAGE_PREFIX + String.format("Ping: &o%s", ping));

    }
}
