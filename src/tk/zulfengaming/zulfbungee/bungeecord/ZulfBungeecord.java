package tk.zulfengaming.zulfbungee.bungeecord;

import net.md_5.bungee.api.plugin.Plugin;
import tk.zulfengaming.zulfbungee.bungeecord.config.YamlConfig;
import tk.zulfengaming.zulfbungee.bungeecord.event.Events;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.task.TaskManager;
import tk.zulfengaming.zulfbungee.bungeecord.task.tasks.CheckUpdateTask;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
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

            getProxy().getPluginManager().registerListener(this, new Events(server));

            taskManager.newTask(server, "MainServer");

        } catch (UnknownHostException e) {
            error("There was an error trying to initialise the server:");
            e.printStackTrace();

        }

        // gets the version from plugin.yml and converts it to an integer array

        String[] versionString = getDescription().getVersion().split("\\.");

        for (int i = 0; i < version.length; i++) {
            version[i] = Integer.parseInt(versionString[i]);
        }

        updater = new CheckUpdateTask(this);

        taskManager.newTask(updater, "UpdateTask");

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

    public boolean isDebug() {
        return isDebug;
    }

    public int[] getVersion() {
        return version;
    }

    public CheckUpdateTask getUpdater() {
        return updater;
    }
}
