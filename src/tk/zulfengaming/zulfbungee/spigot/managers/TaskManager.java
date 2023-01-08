package tk.zulfengaming.zulfbungee.spigot.managers;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scheduler.BukkitTask;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class TaskManager {

    private final ZulfBungeeSpigot instance;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final BukkitScheduler scheduler;

    public TaskManager(ZulfBungeeSpigot instanceIn) {
        this.instance = instanceIn;
        this.scheduler = instance.getServer().getScheduler();
    }

    public void newPluginTask(Plugin pluginIn, Runnable taskIn) {
        scheduler.runTask(pluginIn, taskIn);
    }

    public void newAsyncTask(BukkitRunnable taskIn) {
        taskIn.runTaskAsynchronously(instance);
    }

    public BukkitTask newAsyncTickTask(BukkitRunnable taskIn, int ticks) {
        return taskIn.runTaskTimerAsynchronously(instance, 0, ticks);
    }

    public <T> T submitCallable(Callable<T> callableIn) throws ExecutionException, InterruptedException {
        if (!executorService.isShutdown()) {
            return executorService.submit(callableIn).get();
        } else {
            throw new RejectedExecutionException("Shutting down - callable cannot be submitted.");
        }
    }

    public <T> CompletableFuture<T> submitSupplier(Supplier<T> supplierIn) {
        return CompletableFuture.supplyAsync(supplierIn, executorService);
    }

    public void shutdown() {
        executorService.shutdownNow();
    }
}
