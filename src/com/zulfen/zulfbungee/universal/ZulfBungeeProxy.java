package com.zulfen.zulfbungee.universal;

import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import com.zulfen.zulfbungee.universal.config.ProxyConfig;
import com.zulfen.zulfbungee.universal.managers.ProxyTaskManager;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import com.zulfen.zulfbungee.universal.task.tasks.CheckUpdateTask;
import com.zulfen.zulfbungee.universal.interfaces.NativePlayerConverter;

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
