package tk.zulfengaming.zulfbungee.universal.managers;

public interface ProxyTaskManager {
    void newTask(Runnable taskIn);
    void shutdown();
}
