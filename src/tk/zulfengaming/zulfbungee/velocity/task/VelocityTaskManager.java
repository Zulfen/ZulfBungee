package tk.zulfengaming.zulfbungee.velocity.task;

import tk.zulfengaming.zulfbungee.universal.managers.ProxyTaskManager;
import tk.zulfengaming.zulfbungee.velocity.ZulfVelocity;

public class VelocityTaskManager implements ProxyTaskManager {

    private final ZulfVelocity zulfVelocity;

    public VelocityTaskManager(ZulfVelocity zulfVelocityIn) {
        this.zulfVelocity = zulfVelocityIn;
    }

    @Override
    public void newTask(Runnable taskIn) {
        zulfVelocity.getVelocity().getScheduler().buildTask(zulfVelocity, taskIn).schedule();
    }

}
