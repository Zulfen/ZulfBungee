package tk.zulfengaming.zulfbungee.universal;

import tk.zulfengaming.zulfbungee.universal.config.ProxyConfig;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfServerInfo;
import tk.zulfengaming.zulfbungee.universal.managers.ProxyTaskManager;
import tk.zulfengaming.zulfbungee.universal.task.tasks.CheckUpdateTask;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public interface ZulfBungeeProxy<P> {

    void logDebug(String messageIn);

    void logInfo(String messageIn);

    void error(String messageIn);

    void warning(String messageIn);

    MainServer<P> getServer();

    ProxyTaskManager getTaskManager();
    ProxyConfig getConfig();

    ZulfProxyPlayer<P> getPlayer(UUID uuidIn);
    ZulfProxyPlayer<P> getPlayer(String nameIn);
    Collection<ZulfProxyPlayer<P>> getPlayers();

    ZulfProxyServer<P> getServer(String name);
    Map<String, ZulfServerInfo<P>> getServersCopy();

    String getVersion();
    // make this a path tbh.
    File getPluginFolder();

    P getPlatform();

    CheckUpdateTask<P> getUpdater();

}
