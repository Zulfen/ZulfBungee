package com.zulfen.zulfbungee.velocity.task;

import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.Scheduler;
import com.zulfen.zulfbungee.universal.managers.ProxyTaskManager;
import com.zulfen.zulfbungee.velocity.interfaces.ZulfVelocityImpl;

import java.util.ArrayList;

public class VelocityTaskManager implements ProxyTaskManager {

    private final ZulfVelocityImpl zulfVelocity;

    private final Scheduler scheduler;

    private final ArrayList<ScheduledTask> tasks = new ArrayList<>();

    public VelocityTaskManager(ZulfVelocityImpl zulfVelocityIn) {
        this.zulfVelocity = zulfVelocityIn;
        this.scheduler = zulfVelocity.getPlatform().getScheduler();
    }

    @Override
    public void newTask(Runnable taskIn) {
        tasks.add(scheduler.buildTask(zulfVelocity, taskIn).schedule());
    }

    @Override
    public void shutdown() {
        for (ScheduledTask task : tasks) {
            task.cancel();
        }
    }

}
