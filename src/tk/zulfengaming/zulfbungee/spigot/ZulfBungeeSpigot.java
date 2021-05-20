package tk.zulfengaming.zulfbungee.spigot;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAPIException;
import ch.njol.skript.SkriptAddon;
import org.bukkit.plugin.java.JavaPlugin;
import tk.zulfengaming.zulfbungee.spigot.config.YamlConfig;
import tk.zulfengaming.zulfbungee.spigot.event.EventListeners;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.spigot.task.TaskManager;

import java.io.IOException;
import java.net.UnknownHostException;

public class ZulfBungeeSpigot extends JavaPlugin {

    private static ZulfBungeeSpigot plugin;
    private SkriptAddon addon;

    private boolean debug = false;

    private TaskManager taskManager;
    private YamlConfig config;

    private ClientConnection connection;

    // other

    public void onEnable() {

        plugin = this;

        getServer().getPluginManager().registerEvents(new EventListeners(), this);

        taskManager = new TaskManager(this);
        config = new YamlConfig(this);

        if (config.getBoolean("debug")) {
            debug = true;
        }


        try {
            connection = new ClientConnection(this);

            taskManager.newTask(connection, "MainConnection");

        } catch (UnknownHostException e) {
            error("Error launching connection task!");
            e.printStackTrace();
        }

        addon = Skript.registerAddon(this);

        // Registers the addon
        try {
            addon.loadClasses("tk.zulfengaming.zulfbungee.spigot", "elements");
            logInfo("The addon loaded successfully!");

        } catch (SkriptAPIException | IOException e) {
            error("The addon failed to register! :( please check the error!");
            e.printStackTrace();
        }

    }

    public void onDisable() {

        try {
            connection.shutdown();

        } catch (IOException e) {
            e.printStackTrace();
        }

        taskManager.shutdown();

    }

    public void logDebug(String message) {
        if (debug) {
            getLogger().info(message);
        }
    }

    public void logInfo(String message) {
        getLogger().info(message);
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

    public SkriptAddon getAddon() {
        return addon;
    }

    public boolean isDebug() {
        return debug;
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public ClientConnection getConnection() {
        return connection;
    }

    public static ZulfBungeeSpigot getPlugin() {
        return plugin;
    }

}



