package tk.zulfengaming.zulfbungee.bungeecord.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import tk.zulfengaming.zulfbungee.bungeecord.command.ZulfBungeeCommand;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.CommandHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.ScriptAction;
import tk.zulfengaming.zulfbungee.universal.socket.ScriptInfo;

import java.util.ArrayList;

public class ScriptDelete extends CommandHandler {

    public ScriptDelete(Server serverIn) {
        super(serverIn, "zulfen.admin.script.delete", "scripts", "delete");
    }

    @Override
    public void handleCommand(CommandSender sender, String[] separateArgs) {

        ArrayList<String> availableScripts = getMainServer().getPluginInstance().getConfig().getScriptNames();

        ScriptInfo infoOut;

        if (separateArgs[0].equals("all") && separateArgs.length == 1) {

            infoOut = new ScriptInfo(ScriptAction.DELETE, availableScripts.toArray(new String[0]));
            getMainServer().sendToAllClients(new Packet(PacketTypes.GLOBAL_SCRIPT_HEADER, false, true, infoOut));

        } else {

            StringBuilder scriptName = new StringBuilder();

            for (int i = 0; i < separateArgs.length; i++) {

                scriptName.append(separateArgs[i]);

                if (i != separateArgs.length - 1) {
                    scriptName.append(" ");
                }

            }

            scriptName.append(".sk");

            if (availableScripts.contains(scriptName.toString())) {

                infoOut = new ScriptInfo(ScriptAction.DELETE, new String[]{scriptName.toString()});
                getMainServer().sendToAllClients(new Packet(PacketTypes.GLOBAL_SCRIPT_HEADER, false, true, infoOut));

            } else {

                String logName = scriptName.toString().split(".sk")[0];
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                        ('&', ZulfBungeeCommand.COMMAND_PREFIX + String.format("The script %s does not exist! Please try retyping the command.", logName))));
            }

        }




    }


}
