package com.zulfen.zulfbungee.velocity.task;

import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.Scheduler;
import com.zulfen.zulfbungee.universal.managers.ProxyTaskManager;
import com.zulfen.zulfbungee.velocity.ZulfVelocityMain;
import com.zulfen.zulfbungee.velocity.interfaces.ZulfVelocityImpl;

import java.util.ArrayList;

public class VelocityTaskManager implements ProxyTaskManager {

    private final ZulfVelocityMain pluginInstance;

    private final Scheduler scheduler;

    private final ArrayList<ScheduledTask> tasks = new ArrayList<>();

    public VelocityTaskManager(ZulfVelocityImpl zulfVelocityIn, ZulfVelocityMain pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.scheduler = zulfVelocityIn.getPlatform().getScheduler();
    }

    @Override
    public void newTask(Runnable taskIn) {
        tasks.add(scheduler.buildTask(pluginInstance, taskIn).schedule());
    }

    @Override
    public void shutdown() {
        for (ScheduledTask task : tasks) {
            task.cancel();
        }
    }

}
