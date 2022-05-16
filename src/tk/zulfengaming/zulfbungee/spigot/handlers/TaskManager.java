package tk.zulfengaming.zulfbungee.spigot.handlers;

import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;

import java.util.ArrayList;

public class TaskManager {

    private final ZulfBungeeSpigot instance;

    private final BukkitScheduler scheduler;

    private final ArrayList<BukkitTask> tasks = new ArrayList<>();

    public TaskManager(ZulfBungeeSpigot instanceIn) {
        this.instance = instanceIn;
        this.scheduler = instanceIn.getServer().getScheduler();
    }

    public BukkitTask newTask(Runnable taskIn, String name) {

        BukkitTask theTask = scheduler.runTaskAsynchronously(instance, taskIn);
        tasks.add(theTask);

        return theTask;

    }

    public BukkitTask newRepeatingTask(Runnable taskIn, String name, int ticks) {

        BukkitTask theTask = scheduler.runTaskTimerAsynchronously(instance, taskIn, 0, ticks);
        tasks.add(theTask);

        return theTask;
    }


    public void shutdown() {
        for (BukkitTask task : tasks) {
            task.cancel();
        }
    }
}
