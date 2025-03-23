package com.zulfen.zulfbungee.core.managers;

public interface ProxyTaskManager {
    void newTask(Runnable taskIn);
    void shutdown();
}
