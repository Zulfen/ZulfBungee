package tk.zulfengaming.zulfbungee.universal.command.subcommands;

import tk.zulfengaming.zulfbungee.universal.command.util.Constants;
import tk.zulfengaming.zulfbungee.universal.handlers.CommandHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

public class Ping<P> extends CommandHandler<P> {

    public Ping(MainServer<P> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.ping", "ping");
    }

    @Override
    public void handleCommand(ProxyCommandSender<P> sender, String[] separateArgs) {

        if (sender.isPlayer()) {

            String serverName = ((ZulfProxyPlayer<P>) sender).getServer().getName();
            long ping = getMainServer().getConnectionFromName(serverName).getPing();

            sender.sendMessage(Constants.MESSAGE_PREFIX + String.format("Ping: &o%sms", ping));

        } else {
            sender.sendMessage(Constants.MESSAGE_PREFIX + "Only players can run this command!");
        }


    }
}
