package tk.zulfengaming.zulfbungee.bungeecord.command.subcommands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import tk.zulfengaming.zulfbungee.bungeecord.command.ZulfBungeeCommand;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.CommandHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

        List<String> newScripts = new ArrayList<>();

        if (watchKey != null) {

            for (WatchEvent<?> event : watchKey.pollEvents()) {

                WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) event;

                String scriptName = pathWatchEvent.context().getFileName().toString();

                if (scriptName.endsWith(".sk")) {
                    if (!newScripts.contains(scriptName)) {
                        newScripts.add(scriptName);
                    }
                }

            }

        }

        if (separateArgs[0].equals("all") && separateArgs.length == 1) {

            if (newScripts.isEmpty()) {
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                        ('&', ZulfBungeeCommand.COMMAND_PREFIX + "No scripts have been updated, as they haven't been modified.")));
            } else {
                getMainServer().syncScripts(newScripts);
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                        ('&', ZulfBungeeCommand.COMMAND_PREFIX + String.format("%s script(s) have been updated: %s", newScripts.size(), newScripts))));
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

                getMainServer().syncScripts(Collections.singletonList(scriptName));

            } else {

                String logName = scriptName.split(".sk")[0];
                sender.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                        ('&', ZulfBungeeCommand.COMMAND_PREFIX + String.format("The script %s does not exist! Please try retyping the command.", logName))));

            }

        }

    }
}
