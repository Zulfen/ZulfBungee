package tk.zulfengaming.zulfbungee.universal.managers;

import java.util.concurrent.TimeUnit;

public interface ProxyTaskManager {
    void newTask(Runnable taskIn);
    void newRepeatingTask(Runnable taskIn, long amountIn, TimeUnit timeUnitIn);
    void shutdown();
}
