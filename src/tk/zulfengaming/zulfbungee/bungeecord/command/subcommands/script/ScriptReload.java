package tk.zulfengaming.zulfbungee.bungeecord.command.subcommands.script;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import tk.zulfengaming.zulfbungee.bungeecord.command.ZulfBungeeCommand;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.CommandHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.util.MessageUtils;
import tk.zulfengaming.zulfbungee.universal.socket.ScriptAction;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

import static tk.zulfengaming.zulfbungee.bungeecord.util.MessageUtils.*;

public class ScriptReload extends CommandHandler {

    private final WatchKey watchKey;

    public ScriptReload(Server serverIn) {

        super(serverIn,
                "zulfen.bungee.admin.script",
                "scripts", "reload");

        try {

            WatchService folderWatchService = FileSystems.getDefault().newWatchService();

            this.watchKey = getMainServer().getPluginInstance().getConfig().getScriptsFolderPath()
                    .register(folderWatchService,
                            StandardWatchEventKinds.ENTRY_CREATE,
                            StandardWatchEventKinds.ENTRY_DELETE,
                            StandardWatchEventKinds.ENTRY_MODIFY);


        } catch (IOException e) {
            getMainServer().getPluginInstance().error("There was an error creating a watch service for the scripts folder!");
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


        if (separateArgs.length != 0) {

            if (separateArgs[0].equals("all")) {

                if (scriptsMap.isEmpty()) {
                    sendMessage(sender, "No scripts have been updated, as they haven't been modified.");
                } else {
                    getMainServer().syncScriptsFolder(scriptsMap, sender);
                    sendMessage(sender, String.format("%s script(s) have been updated: %s",
                            scriptsMap.size(), scriptsMap.keySet()));
                }

            } else {

                StringBuilder scriptNameBuilder = new StringBuilder();

                for (int i = 0; i < separateArgs.length; i++) {

                    String arg = separateArgs[i];

                    scriptNameBuilder.append(arg);

                    if (i != separateArgs.length - 1) {
                        scriptNameBuilder.append(" ");
                    } else {
                        if (!arg.endsWith(".sk")) {
                            scriptNameBuilder.append(".sk");
                        }
                    }

                }

                String scriptName = scriptNameBuilder.toString();

                if (getMainServer().getPluginInstance().getConfig().getScripts().contains(scriptName)) {

                    HashMap<String, ScriptAction> tempScriptsMap = new HashMap<>(scriptsMap);
                    tempScriptsMap.keySet().retainAll(Collections.singletonList(scriptName));

                    if (!tempScriptsMap.isEmpty()) {
                        getMainServer().syncScriptsFolder(tempScriptsMap, sender);
                        sendMessage(sender, String.format("Script %s was updated.", scriptName));
                    } else {
                        sendMessage(sender, String.format("The script %s has not been updated!", scriptName));
                    }

                } else {

                    sendMessage(sender, String.format("The script &o%s&r does not exist! Please try retyping the command.", scriptName));

                }
            }

        } else {
            sendMessage(sender, "Please specify a script to reload.");
        }
    }


    @Override
    public Collection<String> onTab(int index) {

        if (index == 0) {

            ArrayList<String> suggestions = new ArrayList<>(getMainServer().getPluginInstance().getConfig().getScripts());

            if (!suggestions.isEmpty()) {
                suggestions.add("all");
                return suggestions;
            }

        }

        return Collections.emptyList();

    }

}
