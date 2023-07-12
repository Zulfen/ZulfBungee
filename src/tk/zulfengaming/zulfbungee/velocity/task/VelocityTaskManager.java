package tk.zulfengaming.zulfbungee.velocity.task;

import com.velocitypowered.api.scheduler.ScheduledTask;
import com.velocitypowered.api.scheduler.Scheduler;
import tk.zulfengaming.zulfbungee.universal.managers.ProxyTaskManager;
import tk.zulfengaming.zulfbungee.velocity.ZulfVelocity;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class VelocityTaskManager implements ProxyTaskManager {

    private final ZulfVelocity zulfVelocity;

    private final Scheduler scheduler;

    private final ArrayList<ScheduledTask> tasks = new ArrayList<>();

    public VelocityTaskManager(ZulfVelocity zulfVelocityIn) {
        this.zulfVelocity = zulfVelocityIn;
        this.scheduler = zulfVelocity.getPlatform().getScheduler();
    }

    @Override
    public void newTask(Runnable taskIn) {
        tasks.add(scheduler.buildTask(zulfVelocity, taskIn).schedule());
    }

    @Override
    public void newRepeatingTask(Runnable taskIn, long amountIn, TimeUnit timeUnitIn) {
        tasks.add(scheduler.buildTask(zulfVelocity, taskIn).repeat(amountIn, timeUnitIn).schedule());
    }

    @Override
    public void shutdown() {
        for (ScheduledTask task : tasks) {
            task.cancel();
        }
    }

}
