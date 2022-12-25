package tk.zulfengaming.zulfbungee.bungeecord.task;

import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import tk.zulfengaming.zulfbungee.bungeecord.ZulfBungeecord;

import java.util.ArrayList;

public class TaskManager {

    private final ZulfBungeecord instance;

    private final TaskScheduler scheduler;

    // keeps track of running stuff
    private final ArrayList<ScheduledTask> tasks = new ArrayList<>();

    public TaskManager(ZulfBungeecord instanceIn) {
        this.instance = instanceIn;
        this.scheduler = instanceIn.getProxy().getScheduler();
    }

    public void newTask(Runnable taskIn) {
        ScheduledTask theTask = scheduler.runAsync(instance, taskIn);
        tasks.add(theTask);
    }

    public void shutdown() {

        for (ScheduledTask task : tasks) {
            task.cancel();
        }

    }

}
