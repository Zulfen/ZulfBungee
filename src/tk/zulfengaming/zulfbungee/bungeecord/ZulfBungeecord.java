package tk.zulfengaming.zulfbungee.bungeecord;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import tk.zulfengaming.zulfbungee.bungeecord.command.ZulfBungeeCommand;
import tk.zulfengaming.zulfbungee.bungeecord.config.YamlConfig;
import tk.zulfengaming.zulfbungee.bungeecord.event.Events;
import tk.zulfengaming.zulfbungee.bungeecord.managers.CommandHandlerManager;
import tk.zulfengaming.zulfbungee.bungeecord.socket.MainServer;
import tk.zulfengaming.zulfbungee.bungeecord.task.TaskManager;
import tk.zulfengaming.zulfbungee.bungeecord.task.tasks.CheckUpdateTask;
import tk.zulfengaming.zulfbungee.bungeecord.util.UpdateResult;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import static tk.zulfengaming.zulfbungee.bungeecord.util.MessageUtils.sendMessage;

public class ZulfBungeecord extends Plugin {

    private Logger logger;

    private YamlConfig config;

    private MainServer mainServer;

    private TaskManager taskManager;

    private CheckUpdateTask updater;

    private final AtomicBoolean isDisabled = new AtomicBoolean(false);

    private boolean isDebug = false;

    public void onEnable() {

        logger = getProxy().getLogger();

        taskManager = new TaskManager(this);

        config = new YamlConfig(this);

        isDebug = config.getBoolean("debug");

        try {

            mainServer = new MainServer(config.getInt("port"), InetAddress.getByName(config.getString("host")), this);

            CommandHandlerManager commandHandlerManager = new CommandHandlerManager(mainServer);

            getProxy().getPluginManager().registerListener(this, new Events(mainServer));
            getProxy().getPluginManager().registerCommand(this, new ZulfBungeeCommand(commandHandlerManager));

            taskManager.newTask(mainServer);

        } catch (UnknownHostException e) {
            error("There was an error trying to initialise the server:");
            e.printStackTrace();

        }

        updater = new CheckUpdateTask(this);

        checkUpdate(getProxy().getConsole(), true);

    }

    @Override
    public void onDisable() {

        try {
            if (isDisabled.compareAndSet(false, true)) {
                mainServer.end();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        taskManager.shutdown();

    }

    public void logDebug(String message) {
        if (isDebug) logger.info("[ZulfBungee] " + message);
    }

    public void logInfo(String message) {
        logger.info("[ZulfBungee] " + message);
    }

    public void error(String message) {
        logger.severe("[ZulfBungee] " + message);
    }

    public void warning(String message) {
        logger.warning("[ZulfBungee] " + message);
    }

    public YamlConfig getConfig() {
        return config;
    }

    public MainServer getServer() {
        return mainServer;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public void checkUpdate(CommandSender sender, boolean notifySuccess) {

        CompletableFuture.supplyAsync(updater)
                .thenAccept(updateResult -> {

                    if (updateResult.isPresent()) {

                        UpdateResult getUpdaterResult = updateResult.get();

                        sendMessage(sender, new ComponentBuilder("A new update to ZulfBungee is available!")
                                .color(ChatColor.WHITE)
                                .append(" (Version " + getUpdaterResult.getLatestVersion() + ")")
                                .italic(true)
                                .color(ChatColor.YELLOW)
                                .create());

                        if (sender instanceof ProxiedPlayer) {

                            sendMessage(sender, new ComponentBuilder("Click this link to get a direct download!")
                                    .color(ChatColor.WHITE)
                                    .underlined(true)
                                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, getUpdaterResult.getDownloadURL()))
                                    .create());

                        } else {

                            sendMessage(sender, new ComponentBuilder("Copy this link into a browser for a direct download!")
                                    .color(ChatColor.WHITE)
                                    .create());
                            sender.sendMessage(new ComponentBuilder(getUpdaterResult.getDownloadURL())
                                    .color(ChatColor.DARK_AQUA)
                                    .underlined(true)
                                    .create());

                        }

                    } else if (notifySuccess) {

                        sendMessage(sender, new ComponentBuilder("ZulfBungee is up to date!")
                                .color(ChatColor.WHITE)
                                .append(" (Version " + getDescription().getVersion() + ")")
                                .italic(true)
                                .color(ChatColor.YELLOW)
                                .create());
                    }

                });

    }

}
