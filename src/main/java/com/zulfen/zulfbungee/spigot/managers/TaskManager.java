package com.zulfen.zulfbungee.spigot.managers;

import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
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

    public void runAsyncTaskLater(Runnable runnableIn, long delayIn) {
        scheduler.runTaskLaterAsynchronously(instance, runnableIn, delayIn);
    }

    public void newAsyncTask(Runnable runnableIn) {
        if (instance.isEnabled()) {
            scheduler.runTaskAsynchronously(instance, runnableIn);
        }
    }

    public <T> T returnableMainThreadTask(Callable<T> callableIn) {
        try {
            return scheduler.callSyncMethod(instance, callableIn).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return null;
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
