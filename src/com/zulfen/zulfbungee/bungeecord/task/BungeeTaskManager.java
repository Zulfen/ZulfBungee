package com.zulfen.zulfbungee.bungeecord.task;

import com.zulfen.zulfbungee.bungeecord.ZulfBungeecord;
import com.zulfen.zulfbungee.universal.managers.ProxyTaskManager;
import net.md_5.bungee.api.scheduler.ScheduledTask;
import net.md_5.bungee.api.scheduler.TaskScheduler;

import java.util.ArrayList;

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

    public void shutdown() {

        for (ScheduledTask task : tasks) {
            task.cancel();
        }

    }

}
