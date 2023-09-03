package com.zulfen.zulfbungee.bungeecord.interfaces;

import com.zulfen.zulfbungee.bungeecord.command.BungeeConsole;
import com.zulfen.zulfbungee.bungeecord.config.BungeeConfig;
import com.zulfen.zulfbungee.bungeecord.objects.BungeePlayer;
import com.zulfen.zulfbungee.bungeecord.objects.BungeeServer;
import com.zulfen.zulfbungee.bungeecord.task.BungeeTaskManager;
import com.zulfen.zulfbungee.universal.ZulfProxyImpl;
import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import com.zulfen.zulfbungee.universal.interfaces.NativePlayerConverter;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ZulfBungeecordImpl implements ZulfProxyImpl<ProxyServer, ProxiedPlayer> {
    
    private final ProxyServer proxy;
    private final Logger logger;
    private final Plugin bungeePlugin;
    
    private final BungeeConfig config;
    private final BungeeTaskManager taskManager;
    private final BungeeConsole console;
    
    private final boolean isDebug;
    private boolean isWaterfall = false;

    private final NativePlayerConverter<ProxyServer, ProxiedPlayer> playerConverter = new NativePlayerConverter<ProxyServer, ProxiedPlayer>() {
        @Override
        public Optional<ZulfProxyPlayer<ProxyServer, ProxiedPlayer>> apply(ProxiedPlayer nativePlayer) {
            if (nativePlayer != null) {
                return Optional.of(new BungeePlayer(nativePlayer, new BungeeServer(nativePlayer.getServer().getInfo())));
            }
            return Optional.empty();
        }
    };

    public ZulfBungeecordImpl(ProxyServer proxyServerIn, Plugin pluginInstance) {
        
        this.proxy = proxyServerIn;
        this.bungeePlugin = pluginInstance;
        this.logger = proxy.getLogger();
        this.config = new BungeeConfig(this);
        this.console = new BungeeConsole(proxy);
        this.taskManager = new BungeeTaskManager(this);
        this.isDebug = config.getBoolean("debug");

        // Used to check if the server is running Waterfall. I could check the server brand, but this is easier.
        try {
            Class.forName("io.github.waterfallmc.waterfall.QueryResult");
            isWaterfall = true;
        } catch (ClassNotFoundException ignored) {
        }
        
    }

    public void logDebug(String message) {
        if (isDebug) logger.info("[ZulfBungee] " + message);
    }

    public void logInfo(String message) {
        logger.info("[ZulfBungee] " + message);
    }

    public void error(String message) {
        logger.severe("[ZulfBungee] " + message);
    }

    public void warning(String message) {
        logger.warning("[ZulfBungee] " + message);
    }

    public BungeeConfig getConfig() {
        return config;
    }

    @Override
    public Optional<ZulfProxyPlayer<ProxyServer, ProxiedPlayer>> getPlayer(String nameIn) {
        ProxiedPlayer player = proxy.getPlayer(nameIn);
        return playerConverter.apply(player);
    }

    @Override
    public Optional<ZulfProxyPlayer<ProxyServer, ProxiedPlayer>> getPlayer(UUID uuidIn) {
        ProxiedPlayer player = proxy.getPlayer(uuidIn);
        return playerConverter.apply(player);
    }

    @Override
    public List<ZulfProxyPlayer<ProxyServer, ProxiedPlayer>> getAllPlayers() {
        return proxy.getServers().values().stream()
                .map(BungeeServer::new)
                .flatMap(server -> server.getPlayers().stream())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ZulfProxyServer<ProxyServer, ProxiedPlayer>> getServer(String serverNameIn) {

        ServerInfo bungeeServerInfo = proxy.getServerInfo(serverNameIn);

        if (bungeeServerInfo != null) {
            return Optional.of(new BungeeServer(bungeeServerInfo));
        }

        return Optional.empty();

    }

    @Override
    public NativePlayerConverter<ProxyServer, ProxiedPlayer> getPlayerConverter() {
        return playerConverter;
    }

    @Override
    public Map<String, ZulfProxyServer<ProxyServer, ProxiedPlayer>> getServersCopy() {

        HashMap<String, ZulfProxyServer<ProxyServer, ProxiedPlayer>>
                serverMap = new HashMap<>();

        for (ServerInfo bungeeInfo : proxy.getServers().values()) {
            serverMap.put(bungeeInfo.getName(), new BungeeServer(bungeeInfo));
        }

        return serverMap;
    }

    @Override
    public void registerMessageChannel(String channelNameIn) {
        proxy.registerChannel(channelNameIn);
    }

    @Override
    public void unregisterMessageChannel(String channelNameIn) {
        proxy.unregisterChannel(channelNameIn);
    }

    @Override
    public void registerServer(String serverNameIn, SocketAddress addressIn) {
        ServerInfo serverInfo = proxy.constructServerInfo(serverNameIn, addressIn, String.format("ZulfBungee registered server (%s)", serverNameIn), false);
        proxy.getServers().put(serverNameIn, serverInfo);
    }

    @Override
    public void deRegisterServer(String serverNameIn) {
        proxy.getServers().remove(serverNameIn);
    }

    @Override
    public void broadcast(String messageIn) {
        proxy.broadcast(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                ('&', messageIn)));
    }

    @Override
    public void broadcast(String messageIn, ZulfProxyServer<ProxyServer, ProxiedPlayer> serverIn) {

        ServerInfo serverInfo = proxy.getServerInfo(serverIn.getName());
        if (serverInfo != null) {
            for (ProxiedPlayer player : serverInfo.getPlayers()) {
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                        ('&', messageIn)));
            }
        }

    }

    @Override
    public String getVersion() {
        return bungeePlugin.getDescription().getVersion();
    }

    @Override
    public Path getPluginFolder() {
        return bungeePlugin.getDataFolder().toPath();
    }

    @Override
    public ProxyCommandSender<ProxyServer, ProxiedPlayer> getConsole() {
        return console;
    }

    @Override
    public ProxyServer getPlatform() {
        return proxy;
    }

    @Override
    public String platformString() {
        return String.format("Bungeecord (%s)", proxy.getVersion());
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public Plugin getBungeePlugin() {
        return bungeePlugin;
    }

    @Override
    public boolean isDebug() {
        return isDebug;
    }

    public BungeeTaskManager getTaskManager() {
        return taskManager;
    }

    public boolean isWaterfall() {
        return isWaterfall;
    }
    
}
