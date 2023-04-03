package tk.zulfengaming.zulfbungee.universal.command.subcommands.script;

import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.command.util.CommandUtils;
import tk.zulfengaming.zulfbungee.universal.command.util.Constants;
import tk.zulfengaming.zulfbungee.universal.handlers.CommandHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptAction;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class ScriptReload<P> extends CommandHandler<P> {

    private final WatchKey watchKey;
    private final Path scriptsFolderPath;

    public ScriptReload(MainServer<P> mainServerIn) {

        super(mainServerIn, "zulfen.bungee.admin.script.reload", "scripts", "reload");

        try {

            WatchService folderWatchService = FileSystems.getDefault().newWatchService();

            this.scriptsFolderPath = getMainServer().getPluginInstance().getConfig()
                    .getScriptsFolderPath();

            this.watchKey = scriptsFolderPath
                    .register(folderWatchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);


        } catch (IOException e) {
            getMainServer().getPluginInstance().error("There was an error creating a watch service for the scripts folder!");
            throw new RuntimeException(e);
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleCommand(ProxyCommandSender<P> sender, String[] separateArgs) {

        HashMap<Path, ScriptAction> scriptsMap = new HashMap<>();

        if (watchKey != null) {

            for (WatchEvent<?> event : watchKey.pollEvents()) {

                WatchEvent.Kind<?> kind = event.kind();
                WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) event;

                Path scriptPath = pathWatchEvent.context();
                String scriptName = scriptPath.getFileName().toString();

                if (scriptName.endsWith(".sk")) {

                    if (!scriptsMap.containsKey(scriptPath)) {

                        if (StandardWatchEventKinds.ENTRY_CREATE.equals(kind)) {

                            if (!scriptName.startsWith("-")) {
                                scriptsMap.put(scriptPath, ScriptAction.NEW);
                            }

                        } else if (StandardWatchEventKinds.ENTRY_MODIFY.equals(kind)) {
                            scriptsMap.put(scriptPath, ScriptAction.RELOAD);
                        } else if (StandardWatchEventKinds.ENTRY_DELETE.equals(kind)) {
                            scriptsMap.put(scriptPath, ScriptAction.DELETE);
                        }

                    }

                }

            }

        }


        if (separateArgs.length != 0) {

            if (separateArgs[0].equals("all")) {

                if (scriptsMap.isEmpty()) {
                    sender.sendMessage(Constants.MESSAGE_PREFIX + "No scripts have been updated, as they haven't been modified.");
                } else {
                    getMainServer().syncScripts(scriptsMap, sender);
                    sender.sendMessage(String.format(Constants.MESSAGE_PREFIX + "%s script(s) have been updated: %s", scriptsMap.size(), scriptsMap.keySet()));
                }

            } else {

                String scriptName = CommandUtils.getScriptNameArgs(separateArgs);
                Path scriptPath = scriptsFolderPath.resolve(scriptName);

                if (Files.exists(scriptPath)) {

                    HashMap<Path, ScriptAction> tempScriptsMap = new HashMap<>(scriptsMap);
                    tempScriptsMap.keySet().retainAll(Collections.singletonList(scriptPath));

                    if (!tempScriptsMap.isEmpty()) {
                        getMainServer().syncScripts(tempScriptsMap, sender);
                        sender.sendMessage(Constants.MESSAGE_PREFIX + String.format("Script %s was updated.", scriptName));
                    } else {
                        sender.sendMessage(Constants.MESSAGE_PREFIX + String.format("The script %s has not been updated!", scriptName));
                    }

                } else {

                    sender.sendMessage(Constants.MESSAGE_PREFIX + String.format("The script &o%s &rdoes not exist! Please try retyping the command.",
                            scriptName));

                }
            }

        } else {
            sender.sendMessage(Constants.MESSAGE_PREFIX + "Please specify a script to reload.");
        }
    }


    @Override
    public Collection<String> onTab(int index) {

        if (index == 0) {

            ArrayList<String> suggestions = new ArrayList<>();

            for (Path path : getMainServer().getPluginInstance().getConfig().getScriptPaths()) {

                String realName = path.getFileName().toString();

                if (!realName.startsWith("-")) {
                    suggestions.add(realName);
                }

            }

           return suggestions;


        }

        return Collections.emptyList();

    }

}
