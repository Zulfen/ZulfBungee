package tk.zulfengaming.zulfbungee.spigot.task;

import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;

import java.util.HashMap;

public class TaskManager {

    private final ZulfBungeeSpigot instance;

    private final BukkitScheduler scheduler;

    // keeps track of running stuff
    private final HashMap<String, BukkitTask> bukkitTasks = new HashMap<>();

    public TaskManager(ZulfBungeeSpigot instanceIn) {
        this.instance = instanceIn;
        this.scheduler = instanceIn.getServer().getScheduler();
    }

    public BukkitTask newTask(Runnable taskIn, String name) {

        BukkitTask theTask = scheduler.runTaskAsynchronously(instance, taskIn);

        bukkitTasks.put(name, theTask);

        return theTask;

    }

    public BukkitTask newRepeatingTask(Runnable taskIn, String name, int ticks) {

        BukkitTask theTask = scheduler.runTaskTimerAsynchronously(instance, taskIn, 0, ticks);

        bukkitTasks.put(name, theTask);

        return theTask;
    }


    public void shutdown() {

        for (BukkitTask task : bukkitTasks.values()) {
            task.cancel();
        }


    }
}
