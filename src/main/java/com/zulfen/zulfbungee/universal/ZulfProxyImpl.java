package com.zulfen.zulfbungee.universal;

import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import com.zulfen.zulfbungee.universal.config.ProxyConfig;
import com.zulfen.zulfbungee.universal.interfaces.NativePlayerConverter;
import com.zulfen.zulfbungee.universal.managers.ProxyTaskManager;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;

import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ZulfProxyImpl<P, T, C> {

    void logDebug(String messageIn);

    void logInfo(String messageIn);

    void error(String messageIn);

    void warning(String messageIn);

    ProxyTaskManager getTaskManager();

    ProxyConfig<P, T, C> getConfig();

    Optional<ZulfProxyPlayer<P, T, C>> getPlayer(String nameIn);
    Optional<ZulfProxyPlayer<P, T, C>> getPlayer(UUID uuidIn);
    default Optional<ZulfProxyPlayer<P, T, C>> getPlayer(ClientPlayer clientPlayerIn) {
        return getPlayer(clientPlayerIn.getUuid());
    }

    List<ZulfProxyPlayer<P, T, C>> getAllPlayers();

    Optional<ZulfProxyServer<P, T, C>> getServer(String serverNameIn);
    default Optional<ZulfProxyServer<P, T, C>> getServer(ClientServer serverIn) {
        return getServer(serverIn.getName());
    }

    NativePlayerConverter<P, T, C> getPlayerConverter();

    Map<String, ZulfProxyServer<P, T, C>> getServersCopy();

    void registerMessageChannel(String channelNameIn);
    void unregisterMessageChannel(String channelNameIn);

    void registerServer(String serverNameIn, SocketAddress addressIn);
    void deRegisterServer(String serverNameIn);

    void broadcast(String messageIn);
    void broadcast(String messageIn, ZulfProxyServer<P, T, C> serverIn);

    String getVersion();

    Path getPluginFolder();

    ProxyCommandSender<P, T, C> getConsole();

    P getPlatform();

    String platformString();

    boolean isDebug();

}
