package tk.zulfengaming.zulfbungee.spigot.managers;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;
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

    public void newAsyncTask(BukkitRunnable taskIn) {
        taskIn.runTaskAsynchronously(instance);
    }

    public void newAsyncTask(Runnable runnableIn) {
        if (instance.isEnabled()) {
            scheduler.runTaskAsynchronously(instance, runnableIn);
        }
    }

    public Future<Void> newMainThreadTask(Callable<Void> callableIn) {
        return scheduler.callSyncMethod(instance, callableIn);
    }

    public <T> CompletableFuture<T> submitSupplier(Supplier<T> supplierIn) {
        return CompletableFuture.supplyAsync(supplierIn, executorService);
    }

    public void shutdown() {
        executorService.shutdownNow();
    }
}
