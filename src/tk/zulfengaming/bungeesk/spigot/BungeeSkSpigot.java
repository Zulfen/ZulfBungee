package tk.zulfengaming.bungeesk.spigot;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAPIException;
import ch.njol.skript.SkriptAddon;
import org.bukkit.plugin.java.JavaPlugin;
import tk.zulfengaming.bungeesk.spigot.config.YamlConfig;
import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;
import tk.zulfengaming.bungeesk.spigot.task.TaskManager;
import tk.zulfengaming.bungeesk.universal.exceptions.TaskAlreadyExists;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class BungeeSkSpigot extends JavaPlugin {

    public BungeeSkSpigot plugin;
    private SkriptAddon addon;

    public boolean debug = false;

    public TaskManager taskManager;
    public YamlConfig config;

    public ClientConnection connection;

    // other

    public void onEnable() {
        plugin = this;
        addon = Skript.registerAddon(plugin);

        taskManager = new TaskManager(plugin);
        config = new YamlConfig(this);

        if (config.getBoolean("debug")) {
            debug = true;
        }

        // Registers the addon
        try {
            addon.loadClasses("tk.zulfengaming.bungeesk.spigot", "elements");
            log("The addon loaded successfully!");
        } catch (SkriptAPIException | IOException e) {
            log("The addon failed to register! :( please check the error!");
            e.printStackTrace();
        }

        try {
            connection = new ClientConnection(this, InetAddress.getByName(config.getString("client-host")), config.getInt("client-port"));

            taskManager.newTask(connection, "MainConnection");

        } catch (UnknownHostException | TaskAlreadyExists e) {
            e.printStackTrace();
        }

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

    public SkriptAddon getAddon() {
        return addon;
    }

}



