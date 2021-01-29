package tk.zulfengaming.bungeesk.spigot.task;

import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot;
import tk.zulfengaming.bungeesk.universal.exceptions.TaskAlreadyExists;

import java.util.HashMap;

public class TaskManager {

    private final BungeeSkSpigot instance;

    private final BukkitScheduler scheduler;

    // keeps track of running shit
    public HashMap<String, BukkitTask> tasks = new HashMap<>();

    public TaskManager(BungeeSkSpigot instanceIn ) {
        this.instance = instanceIn;
        this.scheduler = instanceIn.getServer().getScheduler();
    }

    public void newTask(Runnable taskIn, String name) throws TaskAlreadyExists {
        if (tasks.containsKey(name)) {
            throw new TaskAlreadyExists("Task '"+ name + "' already exists.");
        } else {
            BukkitTask theTask = scheduler.runTaskAsynchronously(instance, taskIn);
            tasks.put(name, theTask);
            instance.log("New task: " + name + " created!");
        }
    }

    public void newRepeatingTask(Runnable taskIn, String name, int ticks) throws TaskAlreadyExists {
        if (tasks.containsKey(name)) {
            throw new TaskAlreadyExists("Task '" + name + "' already exists.");
        } else {
            BukkitTask theTask = scheduler.runTaskTimerAsynchronously(instance, taskIn, 0, ticks);
            tasks.put(name, theTask);
            instance.log("New task: " + name + " created!");
        }
    }

    public void endTask(String name) {
        BukkitTask theTask = tasks.get(name);
        tasks.remove(name);

        theTask.cancel();
        instance.log("Task: " + name + " removed and stopped!");
    }

    public BukkitTask getTask(String name) {
        return tasks.get(name);
    }
}
