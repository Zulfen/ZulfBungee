package tk.zulfengaming.zulfbungee.universal.command.subcommands.script;

import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.command.util.CommandUtils;
import tk.zulfengaming.zulfbungee.universal.command.util.Constants;
import tk.zulfengaming.zulfbungee.universal.config.ProxyConfig;
import tk.zulfengaming.zulfbungee.universal.handlers.CommandHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptAction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class ScriptLoad<P> extends CommandHandler<P> {

    private final ProxyConfig<P> config;

    public ScriptLoad(MainServer<P> mainServerIn) {
        super(mainServerIn, "zulfen.bungee.admin.script.load", "scripts", "load");
        this.config = mainServerIn.getPluginInstance().getConfig();
    }

    @Override
    public void handleCommand(ProxyCommandSender<P> sender, String[] separateArgs) {

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
                sender.sendMessage(Constants.MESSAGE_PREFIX + String.format("The script &o%s &rdoes not exist! Please try retyping the command.",
                        scriptName));
            }

        } else {
            sender.sendMessage(Constants.MESSAGE_PREFIX + "Please specify a script to load.");
        }

    }

    private void loadScript(String scriptName, Path scriptPathIn, ProxyCommandSender<P> senderIn) {

        if (getMainServer().areClientsConnected()) {

            if (!config.isScriptActive(scriptName)) {
                getMainServer().syncScripts(Collections.singletonMap(scriptName, scriptPathIn), ScriptAction.RELOAD, senderIn);
                config.registerScript(scriptName);
                senderIn.sendMessage(Constants.MESSAGE_PREFIX + String.format("Script &o%s &rwas loaded.", scriptName));
            } else {
                senderIn.sendMessage(Constants.MESSAGE_PREFIX + String.format("The script &o%s &rhas already been loaded!",
                        scriptName));
            }

        } else {
            senderIn.sendMessage(Constants.MESSAGE_PREFIX + String.format("Script %s was was not loaded as no clients are connected!", scriptName));
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
