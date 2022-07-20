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
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.task.TaskManager;
import tk.zulfengaming.zulfbungee.bungeecord.task.tasks.CheckUpdateTask;
import tk.zulfengaming.zulfbungee.bungeecord.util.UpdateResult;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

public class ZulfBungeecord extends Plugin {

    private Logger logger;

    private YamlConfig config;

    private Server server;

    private TaskManager taskManager;

    private CheckUpdateTask updater;

    // represents the full version, so like
    // version[0] = 0, version[1] = 6, version[2] = 7
    // there is probably a better way to do this but hey
    private final int[] version = new int[3];

    private boolean isDebug = false;

    public void onEnable() {

        logger = getProxy().getLogger();

        taskManager = new TaskManager(this);

        config = new YamlConfig(this);

        if (config.getBoolean("debug")) isDebug = true;

        try {

            server = new Server(config.getInt("port"), InetAddress.getByName(config.getString("host")), this);

            CommandHandlerManager commandHandlerManager = new CommandHandlerManager(server);

            getProxy().getPluginManager().registerListener(this, new Events(server));
            getProxy().getPluginManager().registerCommand(this, new ZulfBungeeCommand(commandHandlerManager));

            taskManager.newTask(server);

        } catch (UnknownHostException e) {
            error("There was an error trying to initialise the server:");
            e.printStackTrace();

        }

        // gets the version from plugin.yml and converts it to an integer array

        String[] versionString = getDescription().getVersion().split("\\.");

        for (int i = 0; i < versionString.length; i++) {
            version[i] = Integer.parseInt(versionString[i]);
        }

        updater = new CheckUpdateTask(this);

        checkUpdate(getProxy().getConsole());

    }

    @Override
    public void onDisable() {

        try {
            server.end();
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

    public Server getServer() {
        return server;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public int[] getVersion() {
        return version;
    }

    public void checkUpdate(CommandSender sender) {

        CompletableFuture.supplyAsync(updater)
                .thenAccept(updateResult -> {

                    if (updateResult.isPresent()) {

                        UpdateResult getUpdaterResult = updateResult.get();

                        sender.sendMessage(new ComponentBuilder("A new update to ZulfBungee is available!")
                                .color(ChatColor.AQUA)
                                .append(" (Version " + getUpdaterResult.getLatestVersion() + ")")
                                .italic(true)
                                .color(ChatColor.YELLOW)
                                .create());

                        if (sender instanceof ProxiedPlayer) {

                            sender.sendMessage(new ComponentBuilder("Click this link to get a direct download!")
                                    .color(ChatColor.DARK_AQUA)
                                    .underlined(true)
                                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, getUpdaterResult.getDownloadURL()))
                                    .create());

                        } else {

                            sender.sendMessage(new ComponentBuilder("Copy this link into a browser for a direct download!")
                                    .color(ChatColor.AQUA)
                                    .create());
                            sender.sendMessage(new ComponentBuilder(getUpdaterResult.getDownloadURL())
                                    .color(ChatColor.DARK_AQUA)
                                    .underlined(true)
                                    .create());

                        }

                    }

                });

    }

}
