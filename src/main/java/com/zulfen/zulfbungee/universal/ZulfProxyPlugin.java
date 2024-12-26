package com.zulfen.zulfbungee.universal;

import com.zulfen.zulfbungee.universal.config.ProxyConfig;
import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.universal.managers.ProxyTaskManager;
import com.zulfen.zulfbungee.universal.managers.transport.ChannelMainServer;
import com.zulfen.zulfbungee.universal.managers.transport.SocketMainServer;
import com.zulfen.zulfbungee.universal.task.tasks.CheckUpdateTask;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class ZulfProxyPlugin<P, T, C> {

    private final ZulfProxyImpl<P, T, C> zulfBungee;

    private final MainServer<P, T, C> mainServer;
    private final CheckUpdateTask<P, T, C> checkUpdateTask;

    private final ProxyConfig<P, T, C> config;
    private final ProxyTaskManager taskManager;

    public ZulfProxyPlugin(ZulfProxyImpl<P, T, C> zulfBungeeIn) {

        this.zulfBungee = zulfBungeeIn;
        this.checkUpdateTask = new CheckUpdateTask<>(zulfBungee);
        this.taskManager = zulfBungee.getTaskManager();
        this.config = zulfBungee.getConfig();
        String transportType = config.getString("transport-type");

        try {
            if (transportType != null) {
                if (transportType.equalsIgnoreCase("pluginmessage")) {
                    mainServer = new ChannelMainServer<>(zulfBungeeIn, checkUpdateTask);
                } else {
                    mainServer = chooseSocketServer();
                }
            } else {
                mainServer = chooseSocketServer();
            }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }


    }

    private SocketMainServer<P, T, C> chooseSocketServer() throws UnknownHostException {
        SocketMainServer<P, T, C> socketMainServer = new SocketMainServer<>(config.getInt("port"),
                InetAddress.getByName(config.getString("host")), zulfBungee, checkUpdateTask);
        taskManager.newTask(socketMainServer);
        return socketMainServer;
    }

    public MainServer<P, T, C> getMainServer() {
        return mainServer;
    }

    public ProxyTaskManager getTaskManager() {
        return taskManager;
    }

}
