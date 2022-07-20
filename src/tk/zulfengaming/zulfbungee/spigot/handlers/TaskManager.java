package tk.zulfengaming.zulfbungee.spigot.handlers;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class TaskManager {

    private final ZulfBungeeSpigot instance;

    private static final ExecutorService executorService = Executors.newCachedThreadPool();

    public TaskManager(ZulfBungeeSpigot instanceIn) {
        this.instance = instanceIn;
    }

    public void newTask(BukkitRunnable taskIn) {
        taskIn.runTaskAsynchronously(instance);
    }

    public BukkitTask newRepeatingTickTask(BukkitRunnable taskIn, int ticks) {
        return taskIn.runTaskTimerAsynchronously(instance, 0, ticks);
    }

    public <T> T submitCallable(Callable<T> callableIn) throws ExecutionException, InterruptedException {
        return !executorService.isShutdown() ? executorService.submit(callableIn).get() : null;
    }

    public <T> CompletableFuture<T> submitSupplier(Supplier<T> supplierIn) {
        return CompletableFuture.supplyAsync(supplierIn, executorService);
    }

    public void shutdown() {
        executorService.shutdownNow();
    }
}
