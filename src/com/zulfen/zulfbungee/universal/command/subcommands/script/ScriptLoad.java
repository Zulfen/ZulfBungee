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

public class ScriptLoad<P, T> extends CommandHandler<P, T> {

    private final ProxyConfig<P, T> config;

    public ScriptLoad(MainServer<P, T> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.script.load", "scripts", "load");
        this.config = mainServerIn.getPluginInstance().getConfig();
    }

    @Override
    public void handleCommand(ProxyCommandSender<P, T> sender, String[] separateArgs) {

        if (separateArgs.length > 0) {

            Path basePath = config.getScriptsFolderPath();

            String scriptName = CommandUtils.getScriptNameArgs(separateArgs);
            Path unmarkedPath = basePath.resolve(scriptName);
            Path markedPath = basePath.resolve("-" + scriptName);

            if (Files.exists(unmarkedPath)) {

                loadScript(scriptName, unmarkedPath, sender);

            } else if (Files.exists(markedPath)) {

                try {

                    String markedPathString = markedPath.getFileName().toString().substring(1);
                    Path newPath = Files.move(markedPath, markedPath.resolveSibling(markedPathString));

                    loadScript(markedPathString, newPath, sender);

                } catch (IOException e) {
                    getMainServer().getPluginInstance().warning(String.format("There was an error trying to rename script %s whilst trying to load it:", scriptName));
                    e.printStackTrace();
                }

            } else {
                sender.sendPluginMessage(String.format("The script &o%s &rdoes not exist! Please try retyping the command.",
                        scriptName));
            }

        } else {
            sender.sendPluginMessage("Please specify a script to load.");
        }

    }

    private void loadScript(String scriptName, Path scriptPathIn, ProxyCommandSender<P, T> senderIn) {

        if (getMainServer().areClientsConnected()) {

            if (!config.isScriptActive(scriptName)) {
                getMainServer().syncScripts(Collections.singletonMap(scriptName, scriptPathIn), ScriptAction.RELOAD, senderIn);
                config.registerScript(scriptName);
                senderIn.sendPluginMessage(String.format("Script &o%s &rwas loaded.", scriptName));
            } else {
                senderIn.sendPluginMessage(String.format("The script &o%s &rhas already been loaded!",
                        scriptName));
            }

        } else {
            senderIn.sendPluginMessage(String.format("Script %s was was not loaded as no clients are connected!", scriptName));
        }

    }

    @Override
    public Collection<String> onTab(int index) {

        if (index == 0) {

            return getMainServer().getPluginInstance().getConfig().getScriptPaths().stream()
                    .map(path -> path.getFileName().toString())
                    .filter(realName -> realName.startsWith("-"))
                    .map(realName -> realName.substring(1))
                    .collect(Collectors.toList());

        }

        return Collections.emptyList();

    }

}
