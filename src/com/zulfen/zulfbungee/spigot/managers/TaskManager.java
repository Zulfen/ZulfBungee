package com.zulfen.zulfbungee.spigot.managers;

import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitScheduler;

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

    public <T> Future<T> returnableMainThreadTask(Callable<T> callableIn) {
        return scheduler.callSyncMethod(instance, callableIn);
    }

    public void newMainThreadTask(Runnable runnableIn) {
        scheduler.runTask(instance, runnableIn);
    }

    public <T> CompletableFuture<T> submitSupplier(Supplier<T> supplierIn) {
        return CompletableFuture.supplyAsync(supplierIn, executorService);
    }

    public void shutdown() {
        executorService.shutdownNow();
    }
}
