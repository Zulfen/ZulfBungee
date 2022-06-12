package tk.zulfengaming.zulfbungee.bungeecord.task;

import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import tk.zulfengaming.zulfbungee.bungeecord.ZulfBungeecord;

import java.util.HashMap;

public class TaskManager {

    private final ZulfBungeecord instance;

    private final TaskScheduler scheduler;

    // keeps track of running stuff
    private final HashMap<String, ScheduledTask> tasks = new HashMap<>();

    public TaskManager(ZulfBungeecord instanceIn) {
        this.instance = instanceIn;
        this.scheduler = instanceIn.getProxy().getScheduler();
    }

    public void newTask(Runnable taskIn, String name) {
        ScheduledTask theTask = scheduler.runAsync(instance, taskIn);

        tasks.put(name, theTask);

    }

    public void shutdown() {

        for (ScheduledTask task : tasks.values()) {
            task.cancel();
        }

    }

}
