package tk.zulfengaming.bungeesk.bungeecord;

import net.md_5.bungee.api.plugin.Plugin;
import tk.zulfengaming.bungeesk.bungeecord.config.YamlConfig;
import tk.zulfengaming.bungeesk.bungeecord.socket.Server;
import tk.zulfengaming.bungeesk.bungeecord.task.TaskManager;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class BungeeSkProxy extends Plugin {

    private Logger logger;

    private YamlConfig config;

    private Server server;

    private TaskManager taskManager;

    private boolean isDebug = false;

    public void onEnable() {

        logger = getProxy().getLogger();

        taskManager = new TaskManager(this);

        config = new YamlConfig(this);

        if (config.getBoolean("debug")) isDebug = true;

        try {
            server = new Server(config.getInt("port"), InetAddress.getByName(config.getString("host")), this);

            taskManager.newTask(server, "MainServer");

        } catch (UnknownHostException e) {
            error("There was an error trying to initialise the server:");
            e.printStackTrace();

        }

    }

    @Override
    public void onDisable() {

        try {
            server.end();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void log(String message) {
        if (isDebug) logger.info("[BungeeSk] " + message);
    }

    public void error(String message) {
        logger.severe("[BungeeSk] " + message);
    }

    public void warning(String message) {
        logger.warning("[BungeeSk] " + message);
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
}
