package tk.zulfengaming.zulfbungee.spigot;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAPIException;
import ch.njol.skript.SkriptAddon;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import tk.zulfengaming.zulfbungee.spigot.config.YamlConfig;
import tk.zulfengaming.zulfbungee.spigot.event.EventListeners;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.spigot.handlers.TaskManager;

import java.io.IOException;
import java.net.UnknownHostException;

public class ZulfBungeeSpigot extends JavaPlugin {

    // static reference so we can access it via Skript
    private static ZulfBungeeSpigot plugin;

    private boolean debug = false;

    private TaskManager taskManager;
    private YamlConfig config;

    private ClientConnection connection;

    public void onEnable() {

        plugin = this;

        getServer().getPluginManager().registerEvents(new EventListeners(), this);

        taskManager = new TaskManager(this);
        config = new YamlConfig(this);

        debug = config.getBoolean("debug");

        try {

            connection = new ClientConnection(this);

            taskManager.newAsyncTask(connection);

        } catch (UnknownHostException e) {
            error("Error launching connection task!");
            e.printStackTrace();
        }

        SkriptAddon addon = Skript.registerAddon(this);

        // Registers the addon
        try {

            addon.loadClasses("tk.zulfengaming.zulfbungee.spigot", "elements");
            logInfo(ChatColor.GREEN + "The addon loaded successfully!");

        } catch (SkriptAPIException | IOException e) {
            error("The addon failed to register! :( please check the error!");
            e.printStackTrace();
        }

    }

    public void onDisable() {
        connection.shutdown();
        taskManager.shutdown();
    }

    public void logDebug(String message) {
        if (debug) {
            getServer().getConsoleSender().sendMessage("[ZulfBungee] " + message);
        }
    }

    public void logInfo(String message) {
        getServer().getConsoleSender().sendMessage("[ZulfBungee] " + message);
    }

    public void error(String message) {
        getLogger().severe(message);
    }

    public void warning(String message) {
        getLogger().warning(message);
    }

    public YamlConfig getYamlConfig() {
        return config;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public ClientConnection getConnection() {
        return connection;
    }

    // static reference for Skript only
    public static ZulfBungeeSpigot getPlugin() {
        return plugin;
    }

}



