package tk.zulfengaming.bungeesk.bungeecord.task;

import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;
import tk.zulfengaming.bungeesk.universal.exceptions.TaskAlreadyExists;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class TaskManager {

    private BungeeSkProxy instance;

    public TaskScheduler scheduler;

    // keeps track of running shit
    public HashMap<String, ScheduledTask> tasks = new HashMap<>();

    public TaskManager(BungeeSkProxy instanceIn) {
        this.instance = instanceIn;
        this.scheduler = instanceIn.getProxy().getScheduler();
    }

    public void newTask(Runnable taskIn, String name) throws TaskAlreadyExists {
        if (tasks.containsKey(name)) {
            throw new TaskAlreadyExists("Task '"+ name + "' already exists.");
        } else {
            instance.warning(taskIn.toString());
            ScheduledTask theTask = scheduler.runAsync(instance, taskIn);
            tasks.put(name, theTask);
            instance.log("New task: " + name + " created!");
        }
    }

    public void newRepeatingTask(Runnable taskIn, String name, long seconds) throws TaskAlreadyExists {
        if (tasks.containsKey(name)) {
            throw new TaskAlreadyExists("Task '"+ name + "' already exists.");
        } else {
            ScheduledTask theTask = scheduler.schedule(instance, taskIn, seconds, TimeUnit.SECONDS);
            tasks.put(name, theTask);
            instance.log("New repeating task: " + name + " created!");
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
