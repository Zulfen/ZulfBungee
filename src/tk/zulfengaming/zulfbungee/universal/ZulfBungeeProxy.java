package tk.zulfengaming.zulfbungee.universal;

import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.config.ProxyConfig;
import tk.zulfengaming.zulfbungee.universal.managers.ProxyTaskManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfServerInfo;
import tk.zulfengaming.zulfbungee.universal.task.tasks.CheckUpdateTask;

import java.io.File;
import java.util.Map;
import java.util.Optional;

public interface ZulfBungeeProxy<P> {

    void logDebug(String messageIn);

    void logInfo(String messageIn);

    void error(String messageIn);

    void warning(String messageIn);

    ProxyTaskManager getTaskManager();

    ProxyConfig<P> getConfig();

    Optional<ZulfProxyPlayer<P>> getPlayer(String nameIn);
    Optional<ZulfProxyPlayer<P>> getPlayer(ClientPlayer clientPlayerIn);

    Optional<ZulfProxyServer<P>> getServer(ClientServer serverIn);
    Map<String, ZulfServerInfo<P>> getServersCopy();

    void broadcast(String messageIn);
    void broadcast(String messageIn, String serverNameIn);

    String getVersion();

    // make this a path tbh.
    File getPluginFolder();

    ProxyCommandSender<P> getConsole();

    P getPlatform();

    String platformString();

    CheckUpdateTask<P> getUpdater();

    boolean isDebug();

}
