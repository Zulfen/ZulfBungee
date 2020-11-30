package tk.zulfengaming.bungeesk.spigot;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAPIException;
import ch.njol.skript.SkriptAddon;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.io.IOException;

public class BungeeSkSpigot extends JavaPlugin {

    // bukkit shit
    BungeeSkSpigot plugin;
    SkriptAddon addon;

    BukkitScheduler scheduler = getServer().getScheduler();

    // other

    public void onEnable() {
        plugin = this;
        addon = Skript.registerAddon(plugin);

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

    public BungeeSkSpigot getPlugin() {
        return plugin;
    }

    public SkriptAddon getAddon() {
        return addon;
    }

}



