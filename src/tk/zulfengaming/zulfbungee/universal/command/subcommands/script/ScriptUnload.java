package tk.zulfengaming.zulfbungee.universal.command.subcommands.script;

import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.command.util.CommandUtils;
import tk.zulfengaming.zulfbungee.universal.command.util.Constants;
import tk.zulfengaming.zulfbungee.universal.handlers.CommandHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptAction;

import java.util.Collections;

public class ScriptUnload<P> extends CommandHandler<P> {

    public ScriptUnload(MainServer<P> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.script.unload", "scripts", "unload");
    }

    @Override
    public void handleCommand(ProxyCommandSender<P> sender, String[] separateArgs) {

        String scriptName = CommandUtils.getScriptNameArgs(separateArgs);

        if (getMainServer().getPluginInstance().getConfig().getScripts().contains(scriptName)) {

            if (getMainServer().areClientsConnected()) {
                getMainServer().syncScripts(Collections.singletonMap(scriptName, ScriptAction.DELETE), sender);
                sender.sendMessage(Constants.MESSAGE_PREFIX + String.format("Script &o%s &rwas unloaded.", scriptName));
            } else {
                sender.sendMessage(Constants.MESSAGE_PREFIX + String.format("Script &o%s &rwas unloaded as no clients are connected!.", scriptName));
            }


        } else {
            sender.sendMessage(Constants.MESSAGE_PREFIX + String.format("The script &o%s &rdoes not exist! Please try retyping the command.",
                    scriptName));
        }

    }
}
