package tk.zulfengaming.bungeesk.spigot;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAPIException;
import ch.njol.skript.SkriptAddon;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import tk.zulfengaming.bungeesk.spigot.task.TaskManager;

import java.io.IOException;
import java.util.HashMap;

public class BungeeSkSpigot extends JavaPlugin {

    // bukkit shit
    public static BungeeSkSpigot plugin;
    SkriptAddon addon;

    TaskManager taskManager;

    // keeps track of running shit
    public HashMap<String, BukkitTask> tasks;

    // other

    public void onEnable() {
        this.plugin = this;
        this.addon = Skript.registerAddon(plugin);

        this.taskManager = new TaskManager(plugin);

        // Registers the addon
        try {
            addon.loadClasses("tk.zulfengaming.bungeesk.spigot", "elements");
            log("The addon loaded successfully!");
        } catch (SkriptAPIException | IOException e) {
            log("The addon failed to register! :( please check the error!");
            e.printStackTrace();
        }


    }

    public static void log(String message) {
        Bukkit.getLogger().info("[BungeeSk] " + message);
    }

    public static BungeeSkSpigot getPlugin() {
        return plugin;
    }

    public SkriptAddon getAddon() {
        return addon;
    }

}



