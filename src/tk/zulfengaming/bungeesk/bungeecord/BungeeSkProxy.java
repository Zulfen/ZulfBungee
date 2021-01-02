package tk.zulfengaming.bungeesk.bungeecord;

import net.md_5.bungee.api.plugin.Plugin;
import tk.zulfengaming.bungeesk.bungeecord.config.YamlConfig;
import tk.zulfengaming.bungeesk.bungeecord.socket.Server;
import tk.zulfengaming.bungeesk.bungeecord.task.TaskManager;
import tk.zulfengaming.bungeesk.universal.exceptions.TaskAlreadyExists;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Logger;

public class BungeeSkProxy extends Plugin {

    public static BungeeSkProxy plugin;

    public Logger logger;

    public YamlConfig config;

    public Server server;

    public TaskManager taskManager;

    public void onEnable() {
        plugin = this;
        logger = plugin.getLogger();

        taskManager = new TaskManager(this);

        config = new YamlConfig(plugin);

        log("BungeeSk has been enabled!");

        try {
            server = new Server(config.getInt("port"), InetAddress.getByName(config.getString("host")), plugin);
            taskManager.newTask(server, "MainServer");
        } catch (UnknownHostException | TaskAlreadyExists e) {
            e.printStackTrace();
        }
    }

    public static BungeeSkProxy getPlugin() {
        return plugin;
    }

    public void log(String message) {
        logger.info(message);
    }

    public void error(String message) {
        logger.severe(message);
    }

    public void warning(String message) {
        logger.warning(message);
    }
}
