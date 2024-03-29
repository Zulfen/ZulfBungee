package com.zulfen.zulfbungee.universal.command.subcommands.script;

import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ScriptAction;
import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import com.zulfen.zulfbungee.universal.command.util.CommandUtils;
import com.zulfen.zulfbungee.universal.config.ProxyConfig;
import com.zulfen.zulfbungee.universal.handlers.CommandHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class ScriptUnload<P, T> extends CommandHandler<P, T> {

    public ScriptUnload(MainServer<P, T> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.script.unload", "scripts", "unload");
    }

    @Override
    public void handleCommand(ProxyCommandSender<P, T> sender, String[] separateArgs) {

        ProxyConfig<P, T> config = getMainServer().getImpl().getConfig();

        if (separateArgs.length > 0) {

            String scriptName = CommandUtils.getScriptNameArgs(separateArgs);
            Path scriptPath = config.getScriptsFolderPath().resolve(scriptName);

            if (Files.exists(scriptPath)) {

                String fileName = scriptPath.getFileName().toString();

                if (!fileName.startsWith("-")) {

                    if (getMainServer().areClientsConnected()) {

                        getMainServer().syncScripts(Collections.singletonMap(scriptPath, ScriptAction.DELETE), sender);
                        sender.sendPluginMessage(String.format("Script &o%s &rwas unloaded.", scriptName));
                        config.unregisterScript(scriptName);

                        try {
                            Files.move(scriptPath, scriptPath.resolveSibling("-" + fileName));
                        } catch (IOException e) {
                            getMainServer().getImpl().warning(String.format("There was an error trying to rename script %s whilst trying to unload it:", scriptName));
                            e.printStackTrace();
                        }

                    } else {
                        sender.sendPluginMessage(String.format("Script &o%s &rwas not unloaded as no clients are connected!", scriptName));
                    }

                } else {
                    sender.sendPluginMessage(String.format("The script &o%s &rhas already been unloaded!",
                            fileName.substring(1)));
                }

            } else {
                sender.sendPluginMessage(String.format("The script &o%s &rdoes not exist! Please try retyping the command.",
                        scriptName));
            }

        } else {
            sender.sendPluginMessage("Please specify a script to unload.");
        }


    }

    @Override
    public Collection<String> onTab(int index) {

        if (index == 0) {

            return getMainServer().getImpl().getConfig().getScriptPaths().stream()
                    .map(path -> path.getFileName().toString())
                    .filter(realName -> !realName.startsWith("-"))
                    .collect(Collectors.toList());

        }

        return Collections.emptyList();

    }

}
