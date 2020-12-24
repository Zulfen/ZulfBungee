package tk.zulfengaming.bungeesk.spigot.task;

import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import tk.zulfengaming.bungeesk.spigot.BungeeSkSpigot;
import tk.zulfengaming.bungeesk.universal.exceptions.TaskAlreadyExists;

import java.util.HashMap;

public class TaskManager {

    private BungeeSkSpigot instance;

    private BukkitScheduler scheduler;

    // keeps track of running shit
    public HashMap<String, BukkitTask> tasks;

    public TaskManager(BungeeSkSpigot instanceIn ) {
        this.instance = instanceIn;
        this.scheduler = instanceIn.getServer().getScheduler();
    }

    public void newTask(Runnable taskIn, String name) throws TaskAlreadyExists {
        if (tasks.containsValue(name)) {
            throw new TaskAlreadyExists("Task '"+ name + "' already exists.");
        } else {
            BukkitTask theTask = scheduler.runTask(instance, taskIn);
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
