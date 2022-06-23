package tk.zulfengaming.zulfbungee.bungeecord.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import tk.zulfengaming.zulfbungee.bungeecord.command.ZulfBungeeCommand;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.CommandHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.ScriptAction;

import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.HashMap;

public class ScriptReload extends CommandHandler {

    private final WatchKey watchKey;

    public ScriptReload(Server serverIn) {

        super(serverIn,
                "zulfen.admin.script.reload",
                new String[]{"scripts", "reload"},
                new String[]{"all"});

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
            throw new RuntimeException(e);
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleCommand(CommandSender sender, String[] separateArgs) {

        HashMap<String, ScriptAction> scriptsMap = new HashMap<>();

        if (watchKey != null) {

            for (WatchEvent<?> event : watchKey.pollEvents()) {

                WatchEvent.Kind<?> kind = event.kind();
                WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) event;

                String scriptName = pathWatchEvent.context().getFileName().toString();

                if (scriptName.endsWith(".sk")) {

                    if (!scriptsMap.containsKey(scriptName)) {
                        if (StandardWatchEventKinds.ENTRY_CREATE.equals(kind)) {
                            scriptsMap.put(scriptName, ScriptAction.NEW);
                        } else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(kind)) {
                            scriptsMap.put(scriptName, ScriptAction.RELOAD);
                        } else if (StandardWatchEventKinds.ENTRY_DELETE.equals(kind)) {
                            scriptsMap.put(scriptName, ScriptAction.DELETE);
                        }
                    }
                }

            }

        }


        if (separateArgs.length == 1) {

            if (separateArgs[0].equals("all")) {

                if (scriptsMap.isEmpty()) {
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                            ('&', ZulfBungeeCommand.COMMAND_PREFIX + "No scripts have been updated, as they haven't been modified.")));
                } else {
                    getMainServer().syncScriptsFolder(scriptsMap);
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                            ('&', ZulfBungeeCommand.COMMAND_PREFIX + String.format("%s script(s) have been updated: %s", scriptsMap.size(), scriptsMap.keySet()))));
                }

            } else {

                StringBuilder scriptNameBuilder = new StringBuilder();

                for (int i = 0; i < separateArgs.length; i++) {

                    scriptNameBuilder.append(separateArgs[i]);

                    if (i != separateArgs.length - 1) {
                        scriptNameBuilder.append(" ");
                    }

                }

                scriptNameBuilder.append(".sk");

                String scriptName = scriptNameBuilder.toString();

                if (getMainServer().getPluginInstance().getConfig().getScriptNames().contains(scriptName)) {

                    HashMap<String, ScriptAction> tempScriptsMap = new HashMap<>(scriptsMap);
                    tempScriptsMap.keySet().retainAll(Collections.singletonList(scriptName));

                    getMainServer().syncScriptsFolder(tempScriptsMap);

                } else {

                    String logName = scriptName.split(".sk")[0];
                    sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                            ('&', ZulfBungeeCommand.COMMAND_PREFIX + String.format("The script %s does not exist! Please try retyping the command.", logName))));

                }
            }

        } else {
            sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                    ('&', ZulfBungeeCommand.COMMAND_PREFIX + "Please specify a script to reload.")));
        }
    }
}
