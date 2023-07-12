package tk.zulfengaming.zulfbungee.bungeecord.task;

import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;
import tk.zulfengaming.zulfbungee.bungeecord.ZulfBungeecord;
import tk.zulfengaming.zulfbungee.universal.managers.ProxyTaskManager;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class BungeeTaskManager implements ProxyTaskManager {

    private final ZulfBungeecord instance;

    private final TaskScheduler scheduler;

    // keeps track of running stuff
    private final ArrayList<ScheduledTask> tasks = new ArrayList<>();

    public BungeeTaskManager(ZulfBungeecord instanceIn) {
        this.instance = instanceIn;
        this.scheduler = instanceIn.getProxy().getScheduler();
    }

    public void newTask(Runnable taskIn) {
        ScheduledTask theTask = scheduler.runAsync(instance, taskIn);
        tasks.add(theTask);
    }

    @Override
    public void newRepeatingTask(Runnable taskIn, long amountIn, TimeUnit timeUnitIn) {
        scheduler.schedule(instance, taskIn, amountIn, timeUnitIn);
    }

    public void shutdown() {

        for (ScheduledTask task : tasks) {
            task.cancel();
        }

    }

}
