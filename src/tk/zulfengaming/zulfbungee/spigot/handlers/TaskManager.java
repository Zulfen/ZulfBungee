package tk.zulfengaming.zulfbungee.spigot.handlers;

import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;

import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.function.Supplier;

public class TaskManager {

    private final ZulfBungeeSpigot instance;

    private final BukkitScheduler scheduler;

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    private final ArrayList<BukkitTask> bukkitTasks = new ArrayList<>();

    public TaskManager(ZulfBungeeSpigot instanceIn) {
        this.instance = instanceIn;
        this.scheduler = instanceIn.getServer().getScheduler();
    }

    public BukkitTask newTask(Runnable taskIn) {
        BukkitTask bukkitTask = scheduler.runTaskAsynchronously(instance, taskIn);
        bukkitTasks.add(bukkitTask);
        return bukkitTask;
    }

    public BukkitTask newRepeatingTask(Runnable taskIn, int ticks) {
        BukkitTask bukkitTask = scheduler.runTaskTimerAsynchronously(instance, taskIn, 0, ticks);
        bukkitTasks.add(bukkitTask);
        return bukkitTask;
    }

    public <T> T submitCallable(Callable<T> callableIn) throws ExecutionException, InterruptedException {
        return !executorService.isShutdown() ? executorService.submit(callableIn).get() : null;
    }

    public <T> CompletableFuture<T> submitSupplier(Supplier<T> supplierIn) {
        return CompletableFuture.supplyAsync(supplierIn, executorService);
    }

    public void shutdown() {

        executorService.shutdown();

        for (BukkitTask task : bukkitTasks) {
            task.cancel();
        }

    }
}
