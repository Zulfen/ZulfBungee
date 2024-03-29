package com.zulfen.zulfbungee.universal.command.subcommands.script;

import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ScriptAction;
import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import com.zulfen.zulfbungee.universal.command.util.CommandUtils;
import com.zulfen.zulfbungee.universal.handlers.CommandHandler;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class ScriptReload<P, T> extends CommandHandler<P, T> {

    private final WatchKey watchKey;
    private final Path scriptsFolderPath;

    public ScriptReload(MainServer<P, T> mainServerIn) {

        super(mainServerIn, "zulfen.bungee.admin.script.reload", "scripts", "reload");

        try {

            WatchService folderWatchService = FileSystems.getDefault().newWatchService();

            this.scriptsFolderPath = getMainServer().getImpl().getConfig()
                    .getScriptsFolderPath();

            this.watchKey = scriptsFolderPath
                    .register(folderWatchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY);


        } catch (IOException e) {
            getMainServer().getImpl().error("There was an error creating a watch service for the scripts folder!");
            throw new RuntimeException(e);
        }

    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleCommand(ProxyCommandSender<P, T> sender, String[] separateArgs) {

        HashMap<Path, ScriptAction> scriptsMap = new HashMap<>();

        if (watchKey != null) {

            for (WatchEvent<?> event : watchKey.pollEvents()) {

                WatchEvent.Kind<?> kind = event.kind();
                WatchEvent<Path> pathWatchEvent = (WatchEvent<Path>) event;

                Path scriptPath = scriptsFolderPath.resolve(pathWatchEvent.context());
                String scriptName = scriptPath.getFileName().toString();

                if (scriptName.endsWith(".sk")) {

                    if (!scriptsMap.containsKey(scriptPath)) {

                        if (StandardWatchEventKinds.ENTRY_MODIFY.equals(kind) || StandardWatchEventKinds.ENTRY_CREATE.equals(kind)) {
                            scriptsMap.put(scriptPath, ScriptAction.RELOAD);
                        } else if (StandardWatchEventKinds.ENTRY_DELETE.equals(kind)) {
                            scriptsMap.put(scriptPath, ScriptAction.DELETE);
                        }

                    }

                }

            }

            watchKey.reset();

        }

        if (separateArgs.length != 0) {

            if (separateArgs[0].equals("all")) {

                if (scriptsMap.isEmpty()) {
                    sender.sendPluginMessage("No scripts have been updated, as they haven't been modified.");
                } else {

                    getMainServer().syncScripts(scriptsMap, sender);

                    List<String> scriptNames = scriptsMap.keySet().stream()
                            .map(Path::getFileName)
                            .map(Path::toString)
                            .collect(Collectors.toList());

                    sender.sendPluginMessage(String.format("%s script(s) have been updated: %s", scriptsMap.size(), scriptNames));

                }

            } else {

                String scriptName = CommandUtils.getScriptNameArgs(separateArgs);
                Path scriptPath = scriptsFolderPath.resolve(scriptName);

                if (Files.exists(scriptPath)) {

                    scriptsMap.keySet().retainAll(Collections.singletonList(scriptPath));

                    if (!scriptsMap.isEmpty()) {
                        getMainServer().syncScripts(scriptsMap, sender);
                        sender.sendPluginMessage(String.format("Script %s was updated.", scriptName));
                    } else {
                        sender.sendPluginMessage(String.format("The script %s has not been updated!", scriptName));
                    }

                } else {

                    sender.sendPluginMessage(String.format("The script &o%s &rdoes not exist! Please try retyping the command.",
                            scriptName));

                }
            }

        } else {
            sender.sendPluginMessage("Please specify a script to reload.");
        }
    }


    @Override
    public Collection<String> onTab(int index) {

        if (index == 0) {

            ArrayList<String> suggestions = new ArrayList<>();

            for (Path path : getMainServer().getImpl().getConfig().getScriptPaths()) {

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
