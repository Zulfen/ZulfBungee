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

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;

public class ScriptReload extends CommandHandler {

    private WatchKey watchKey;

    public ScriptReload(Server serverIn) {

        super(serverIn, "zulfen.admin.script.reload", "scripts", "reload");

        try {

            WatchService folderWatchService = FileSystems.getDefault().newWatchService();

            this.watchKey = getMainServer().getPluginInstance().getConfig().getScriptsFolderPath()
                    .register(folderWatchService,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.ENTRY_MODIFY);


        } catch (IOException e) {
            getMainServer().getPluginInstance().error("There was an error creating a watch service for the scripts folder!");
            e.printStackTrace();
        }

    }

    @Override
    public void handleCommand(CommandSender sender, String[] separateArgs) {

        ArrayList<String> newScripts = getMainServer().getPluginInstance().getConfig().getScriptNames();

        if (watchKey != null) {

            for (WatchEvent<?> event : watchKey.pollEvents()) {

                WatchEvent.Kind<?> kind = event.kind();
                WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) event;

                String scriptName = pathWatchEvent.context().getFileName().toString();

                if (StandardWatchEventKinds.ENTRY_CREATE.equals(kind) || StandardWatchEventKinds.ENTRY_MODIFY.equals(kind)) {
                    newScripts.add(scriptName);
                } else if (StandardWatchEventKinds.ENTRY_DELETE.equals(kind)) {
                    newScripts.remove(scriptName);
                }

            }

        }

        if (separateArgs[0].equals("all") && separateArgs.length == 1) {

            getMainServer().sendToAllClients(new Packet(PacketTypes.GLOBAL_SCRIPT_HEADER, false, true, newScripts.toArray(new String[0])));

        } else {

            ScriptInfo infoOut;

            StringBuilder scriptName = new StringBuilder();

            for (int i = 0; i < separateArgs.length; i++) {

                scriptName.append(separateArgs[i]);

                if (i != separateArgs.length - 1) {
                    scriptName.append(" ");
                }

            }

            scriptName.append(".sk");

            if (newScripts.contains(scriptName.toString())) {

                infoOut = new ScriptInfo(ScriptAction.RELOAD, new String[]{scriptName.toString()});
                getMainServer().sendToAllClients(new Packet(PacketTypes.GLOBAL_SCRIPT_HEADER, false, true, infoOut));

            } else {


                if (newScripts.contains(scriptName.toString())) {

                    infoOut = new ScriptInfo(ScriptAction.NEW, new String[]{scriptName.toString()});
                    getMainServer().sendToAllClients(new Packet(PacketTypes.GLOBAL_SCRIPT_HEADER, false, true, infoOut));

                } else {

                    String logName = scriptName.toString().split(".sk")[0];
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                            ('&', ZulfBungeeCommand.COMMAND_PREFIX + String.format("The script %s does not exist! Please try retyping the command.", logName))));

                }

            }

        }




    }


}
