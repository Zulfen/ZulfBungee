package tk.zulfengaming.zulfbungee.spigot.handlers;

import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;

import java.util.ArrayList;
import java.util.concurrent.*;

public class TaskManager {

    private final ZulfBungeeSpigot instance;

    private final BukkitScheduler scheduler;

    private final ExecutorService executorService;

    public TaskManager(ZulfBungeeSpigot instanceIn) {
        this.instance = instanceIn;
        this.scheduler = instanceIn.getServer().getScheduler();
        this.executorService = Executors.newSingleThreadExecutor();
    }

    public BukkitTask newTask(Runnable taskIn) {
        return scheduler.runTaskAsynchronously(instance, taskIn);
    }

    public BukkitTask newRepeatingTask(Runnable taskIn, int ticks) {
        return scheduler.runTaskTimerAsynchronously(instance, taskIn, 0, ticks);
    }

    public <T> T submitCallable(Callable<T> callableIn) throws ExecutionException, InterruptedException {
        return executorService.submit(callableIn).get();
    }

    public void shutdown() {
        executorService.shutdown();
    }
}
