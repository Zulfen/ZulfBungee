package tk.zulfengaming.zulfbungee.universal.command.subcommands.script;

import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.command.util.CommandUtils;
import tk.zulfengaming.zulfbungee.universal.command.util.Constants;
import tk.zulfengaming.zulfbungee.universal.handlers.CommandHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptAction;

import java.util.Collections;

public class ScriptLoad<P> extends CommandHandler<P> {

    public ScriptLoad(MainServer<P> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.script.load", "scripts", "load");
    }

    @Override
    public void handleCommand(ProxyCommandSender<P> sender, String[] separateArgs) {

        String scriptName = CommandUtils.getScriptNameArgs(separateArgs);

        if (getMainServer().getPluginInstance().getConfig().getScripts().contains(scriptName)) {

            if (getMainServer().areClientsConnected()) {
                getMainServer().syncScripts(Collections.singletonMap(scriptName, ScriptAction.NEW), sender);
                sender.sendMessage(Constants.MESSAGE_PREFIX + String.format("Script &o%s &rwas loaded.", scriptName));
            }

            sender.sendMessage(Constants.MESSAGE_PREFIX + String.format("Script %s was was not sent as no clients are connected!", scriptName));

        } else {
            sender.sendMessage(Constants.MESSAGE_PREFIX + String.format("The script &o%s &rdoes not exist! Please try retyping the command.",
                    scriptName));
        }

    }
}
