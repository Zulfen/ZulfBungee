package tk.zulfengaming.bungeesk.spigot;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAPIException;
import ch.njol.skript.SkriptAddon;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import tk.zulfengaming.bungeesk.spigot.config.YamlConfig;
import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;
import tk.zulfengaming.bungeesk.spigot.task.TaskManager;

import java.io.IOException;
import java.net.UnknownHostException;

public class BungeeSkSpigot extends JavaPlugin {

    private static BungeeSkSpigot plugin;
    private SkriptAddon addon;

    private boolean debug = false;

    private TaskManager taskManager;
    private YamlConfig config;

    private ClientConnection connection;

    // other

    public void onEnable() {

        plugin = this;

        taskManager = new TaskManager(this);
        config = new YamlConfig(this);

        if (config.getBoolean("debug")) {
            debug = true;
        }


        try {
            connection = new ClientConnection(this);

            BukkitTask serverTask = taskManager.newTask(connection, "MainConnection");

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        addon = Skript.registerAddon(this);

        // Registers the addon
        try {
            addon.loadClasses("tk.zulfengaming.bungeesk.spigot", "elements");
            log("The addon loaded successfully!");
        } catch (SkriptAPIException | IOException e) {
            log("The addon failed to register! :( please check the error!");
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

    public void log(String message) {
        if (debug) {
            getLogger().info(message);
        }
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

    public static BungeeSkSpigot getPlugin() {
        return plugin;
    }

}



