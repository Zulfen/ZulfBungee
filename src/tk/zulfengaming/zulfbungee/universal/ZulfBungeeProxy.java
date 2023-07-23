package tk.zulfengaming.zulfbungee.universal;

import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.config.ProxyConfig;
import tk.zulfengaming.zulfbungee.universal.interfaces.NativePlayerConverter;
import tk.zulfengaming.zulfbungee.universal.managers.ProxyTaskManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import tk.zulfengaming.zulfbungee.universal.task.tasks.CheckUpdateTask;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ZulfBungeeProxy<P, T> {

    void logDebug(String messageIn);

    void logInfo(String messageIn);

    void error(String messageIn);

    void warning(String messageIn);

    ProxyTaskManager getTaskManager();

    ProxyConfig<P, T> getConfig();

    Optional<ZulfProxyPlayer<P, T>> getPlayer(String nameIn);
    default Optional<ZulfProxyPlayer<P, T>> getPlayer(ClientPlayer clientPlayerIn) {
        return getPlayer(clientPlayerIn.getName());
    }

    List<ZulfProxyPlayer<P, T>> getAllPlayers();

    Optional<ZulfProxyServer<P, T>> getServer(String serverNameIn);
    default Optional<ZulfProxyServer<P, T>> getServer(ClientServer serverIn) {
        return getServer(serverIn.getName());
    }

    NativePlayerConverter<P, T> getPlayerConverter();

    Map<String, ZulfProxyServer<P, T>> getServersCopy();

    void registerMessageChannel(String channelNameIn);
    void unregisterMessageChannel(String channelNameIn);

    void broadcast(String messageIn);
    void broadcast(String messageIn, ZulfProxyServer<P, T> serverIn);

    String getVersion();

    Path getPluginFolder();

    ProxyCommandSender<P, T> getConsole();

    P getPlatform();

    String platformString();

    CheckUpdateTask<P, T> getUpdater();

    boolean isDebug();

}
