package tk.zulfengaming.bungeesk.bungeecord.task;

import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import tk.zulfengaming.bungeesk.bungeecord.BungeeSkProxy;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TaskManager {

    private final ExecutorService executorService;
    private final BungeeSkProxy instance;

    public TaskScheduler scheduler;

    // keeps track of running shit
    public HashMap<String, ScheduledTask> tasks = new HashMap<>();

    public TaskManager(BungeeSkProxy instanceIn) {
        this.instance = instanceIn;
        this.scheduler = instanceIn.getProxy().getScheduler();
        this.executorService = Executors.newFixedThreadPool(10);
    }

    public ScheduledTask newTask(Runnable taskIn, String name) {
        ScheduledTask theTask = scheduler.runAsync(instance, taskIn);

        tasks.put(name, theTask);

        return theTask;
    }

    public ScheduledTask newRepeatingTask(Runnable taskIn, String name, long ms) {
        ScheduledTask theTask = scheduler.schedule(instance, taskIn, ms, TimeUnit.MILLISECONDS);

        tasks.put(name, theTask);

        return theTask;
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

    public ExecutorService getExecutorService() {
        return executorService;
    }

}