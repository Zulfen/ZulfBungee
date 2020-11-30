package tk.zulfengaming.bungeesk.bungeecord;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import tk.zulfengaming.bungeesk.bungeecord.config.YamlConfig;
import tk.zulfengaming.bungeesk.bungeecord.socket.Server;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.logging.Logger;

public class BungeeSkProxy extends Plugin {

    private BungeeSkProxy plugin;

    public Logger logger;

    public TaskScheduler scheduler;

    public YamlConfig config;

    public Server socket;

    // keeps track of running shit
    public HashMap tasks;

    public void onEnable() {
        plugin = this;
        logger = plugin.getLogger();

        config = new YamlConfig(plugin);

        log("BungeeSk has been enabled!");

        try {
            socket = new Server(config.getInt("port"), InetAddress.getByName(config.getString("host")), plugin);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        socket.start();
    }

    public BungeeSkProxy getPlugin() {
        return plugin;
    }

    public void log(String message) {
        logger.info("[BungeeSk] " + message);
    }

    public void newTask(Runnable taskIn) {
        ScheduledTask theTask = scheduler.runAsync(this, taskIn);
        tasks.add(theTask);
    }
}
