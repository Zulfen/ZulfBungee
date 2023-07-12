package tk.zulfengaming.zulfbungee.bungeecord;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import tk.zulfengaming.zulfbungee.bungeecord.command.BungeeCommand;
import tk.zulfengaming.zulfbungee.bungeecord.command.BungeeConsole;
import tk.zulfengaming.zulfbungee.bungeecord.config.BungeeConfig;
import tk.zulfengaming.zulfbungee.bungeecord.event.BungeeEvents;
import tk.zulfengaming.zulfbungee.bungeecord.objects.BungeePlayer;
import tk.zulfengaming.zulfbungee.bungeecord.objects.BungeeServer;
import tk.zulfengaming.zulfbungee.bungeecord.task.BungeeTaskManager;
import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.interfaces.MessageCallback;
import tk.zulfengaming.zulfbungee.universal.interfaces.NativePlayerConverter;
import tk.zulfengaming.zulfbungee.universal.managers.CommandHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.ChannelMainServer;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.SocketMainServer;
import tk.zulfengaming.zulfbungee.universal.socket.SocketServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import tk.zulfengaming.zulfbungee.universal.task.tasks.CheckUpdateTask;

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

    private String transportType;

    private final BungeeConsole console = new BungeeConsole(getProxy());

    private final NativePlayerConverter<ProxiedPlayer, ProxyServer> playerConverter = new NativePlayerConverter<ProxiedPlayer, ProxyServer>() {
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

            transportType = config.getString("transport-type");

            if (transportType.equalsIgnoreCase("pluginmessage")) {
                mainServer = new ChannelMainServer<>(this);
            } else if (transportType.equalsIgnoreCase("socket")) {
                SocketMainServer<ProxyServer, ProxiedPlayer> socketMainServer = new SocketMainServer<>(config.getInt("port"), InetAddress.getByName(config.getString("host")), this);
                bungeeTaskManager.newTask(socketMainServer);
                mainServer = socketMainServer;
            } else {
                throw new RuntimeException("Invalid transport type chosen! Please check the config.");
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
    public NativePlayerConverter<ProxiedPlayer, ProxyServer> getPlayerConverter() {
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
    public Optional<MessageCallback> getMessagingCallback(String channelNameIn, String serverNameIn) {
        ServerInfo serverInfo = getProxy().getServerInfo(serverNameIn);
        if (serverInfo != null) {
            return Optional.of(dataIn -> serverInfo.sendData(channelNameIn, dataIn));
        }
        return Optional.empty();
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
    public String getTransportType() {
        return transportType;
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
