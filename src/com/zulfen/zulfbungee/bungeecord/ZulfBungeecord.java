package com.zulfen.zulfbungee.bungeecord;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import com.zulfen.zulfbungee.bungeecord.command.BungeeCommand;
import com.zulfen.zulfbungee.bungeecord.command.BungeeConsole;
import com.zulfen.zulfbungee.bungeecord.config.BungeeConfig;
import com.zulfen.zulfbungee.bungeecord.event.BungeeEvents;
import com.zulfen.zulfbungee.bungeecord.objects.BungeePlayer;
import com.zulfen.zulfbungee.bungeecord.objects.BungeeServer;
import com.zulfen.zulfbungee.bungeecord.task.BungeeTaskManager;
import com.zulfen.zulfbungee.universal.ZulfBungeeProxy;
import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import com.zulfen.zulfbungee.universal.interfaces.NativePlayerConverter;
import com.zulfen.zulfbungee.universal.managers.CommandHandlerManager;
import com.zulfen.zulfbungee.universal.managers.transport.ChannelMainServer;
import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.universal.managers.transport.SocketMainServer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import com.zulfen.zulfbungee.universal.task.tasks.CheckUpdateTask;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ZulfBungeecord extends Plugin implements ZulfBungeeProxy<ProxyServer, ProxiedPlayer> {

    private Logger logger;

    private BungeeConfig config;
    private MainServer<ProxyServer, ProxiedPlayer> mainServer;
    private BungeeTaskManager bungeeTaskManager;

    private CheckUpdateTask<ProxyServer, ProxiedPlayer> updater;
    private boolean isDebug = false;

    private final BungeeConsole console = new BungeeConsole(getProxy());

    private final NativePlayerConverter<ProxyServer, ProxiedPlayer> playerConverter = new NativePlayerConverter<ProxyServer, ProxiedPlayer>() {
        @Override
        public Optional<ZulfProxyPlayer<ProxyServer, ProxiedPlayer>> apply(ProxiedPlayer nativePlayer) {
            if (nativePlayer != null) {
                return Optional.of(new BungeePlayer(nativePlayer, new BungeeServer(nativePlayer.getServer().getInfo())));
            }
            return Optional.empty();
        }
    };

    public void onEnable() {

        logger = getProxy().getLogger();
        bungeeTaskManager = new BungeeTaskManager(this);
        config = new BungeeConfig(this);
        isDebug = config.getBoolean("debug");
        updater = new CheckUpdateTask<>(this);

        try {

            String transportType = config.getString("transport-type");

            if (transportType.equalsIgnoreCase("pluginmessage")) {
                mainServer = new ChannelMainServer<>(this);
            } else {
                SocketMainServer<ProxyServer, ProxiedPlayer> socketMainServer = new SocketMainServer<>(config.getInt("port"), InetAddress.getByName(config.getString("host")), this);
                bungeeTaskManager.newTask(socketMainServer);
                mainServer = socketMainServer;
            }

            CommandHandlerManager<ProxyServer, ProxiedPlayer> commandHandlerManager = new CommandHandlerManager<>(mainServer);

            getProxy().getPluginManager().registerListener(this, new BungeeEvents(mainServer));
            getProxy().getPluginManager().registerCommand(this, new BungeeCommand(commandHandlerManager));



        } catch (UnknownHostException e) {
            error("Could not start the server! (bungeecord)");
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {

        try {

            mainServer.end();

        } catch (IOException e) {
            e.printStackTrace();
        }

        bungeeTaskManager.shutdown();

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
        ProxiedPlayer player = getProxy().getPlayer(nameIn);
        return playerConverter.apply(player);
    }

    @Override
    public List<ZulfProxyPlayer<ProxyServer, ProxiedPlayer>> getAllPlayers() {
        return getProxy().getServers().values().stream()
                .map(BungeeServer::new)
                .flatMap(server -> server.getPlayers().stream())
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ZulfProxyServer<ProxyServer, ProxiedPlayer>> getServer(String serverNameIn) {

        ServerInfo bungeeServerInfo = getProxy().getServerInfo(serverNameIn);

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

        for (ServerInfo bungeeInfo : getProxy().getServers().values()) {
            serverMap.put(bungeeInfo.getName(), new BungeeServer(bungeeInfo));
        }

        return serverMap;
    }

    @Override
    public void registerMessageChannel(String channelNameIn) {
        getProxy().registerChannel(channelNameIn);
    }

    @Override
    public void unregisterMessageChannel(String channelNameIn) {
        getProxy().unregisterChannel(channelNameIn);
    }

    @Override
    public void broadcast(String messageIn) {
        getProxy().broadcast(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                ('&', messageIn)));
    }

    @Override
    public void broadcast(String messageIn, ZulfProxyServer<ProxyServer, ProxiedPlayer> serverIn) {

        ServerInfo serverInfo = getProxy().getServerInfo(serverIn.getName());
        if (serverInfo != null) {
            for (ProxiedPlayer player : serverInfo.getPlayers()) {
                player.sendMessage(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                        ('&', messageIn)));
            }
        }

    }

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }

    @Override
    public Path getPluginFolder() {
        return getDataFolder().toPath();
    }

    @Override
    public ProxyCommandSender<ProxyServer, ProxiedPlayer> getConsole() {
        return console;
    }

    @Override
    public ProxyServer getPlatform() {
        return getProxy();
    }

    @Override
    public String platformString() {
        return String.format("Bungeecord (%s)", getProxy().getVersion());
    }

    @Override
    public CheckUpdateTask<ProxyServer, ProxiedPlayer> getUpdater() {
        return updater;
    }

    @Override
    public boolean isDebug() {
        return isDebug;
    }

    public BungeeTaskManager getTaskManager() {
        return bungeeTaskManager;
    }

}
