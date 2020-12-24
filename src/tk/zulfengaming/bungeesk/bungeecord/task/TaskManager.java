package tk.zulfengaming.bungeesk.bungeecord.task;

import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;
import tk.zulfengaming.bungeesk.universal.exceptions.TaskAlreadyExists;

import java.util.HashMap;

public class TaskManager {

    private BungeeSkProxy instance;

    private TaskScheduler scheduler;

    // keeps track of running shit
    public HashMap<String, ScheduledTask> tasks;

    public TaskManager(BungeeSkProxy instanceIn) {
        this.instance = instanceIn;
    }

    public void newTask(Runnable taskIn, String name) throws TaskAlreadyExists {
        if (tasks.containsValue(name)) {
            throw new TaskAlreadyExists("Task '"+ name + "' already exists.");
        } else {
            ScheduledTask theTask = scheduler.runAsync(instance, taskIn);
            tasks.put(name, theTask);
            instance.log("New task: " + name + " created!");
        }
    }

    public void endTask(String name) {
        ScheduledTask theTask = tasks.get(name);
        tasks.remove(name);
        theTask.cancel();
        instance.log("Task: " + name + " removed and stopped!");
    }

    public ScheduledTask getTask(String name) {
        return tasks.get(name);
    }
}
