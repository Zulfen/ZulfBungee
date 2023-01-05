package tk.zulfengaming.zulfbungee.bungeecord;

import net.md_5.bungee.api.ProxyServer;
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
import tk.zulfengaming.zulfbungee.universal.managers.CommandHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfServerInfo;
import tk.zulfengaming.zulfbungee.universal.task.tasks.CheckUpdateTask;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ZulfBungeecord extends Plugin implements ZulfBungeeProxy<ProxyServer> {

    private Logger logger;

    private BungeeConfig config;

    private MainServer<ProxyServer> mainServer;

    private BungeeTaskManager bungeeTaskManager;

    private CheckUpdateTask<ProxyServer> updater;

    private boolean isDebug = false;

    public void onEnable() {

        logger = getProxy().getLogger();

        bungeeTaskManager = new BungeeTaskManager(this);

        config = new BungeeConfig(this);

        isDebug = config.getBoolean("debug");

        updater = new CheckUpdateTask<>(this);


        try {

            mainServer = new MainServer<>(config.getInt("port"), InetAddress.getByName(config.getString("host")), this);

            CommandHandlerManager<ProxyServer> commandHandlerManager = new CommandHandlerManager<>(mainServer);

            getProxy().getPluginManager().registerListener(this, new BungeeEvents(mainServer));
            getProxy().getPluginManager().registerCommand(this, new BungeeCommand(commandHandlerManager));

            bungeeTaskManager.newTask(mainServer);

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
    public ZulfProxyPlayer<ProxyServer> getPlayer(UUID uuidIn) {

        ProxiedPlayer player = getProxy().getPlayer(uuidIn);

        if (player != null) {
            return new BungeePlayer<>(player);
        }

        return null;
    }

    @Override
    public ZulfProxyPlayer<ProxyServer> getPlayer(String nameIn) {

        ProxiedPlayer player = getProxy().getPlayer(nameIn);

        if (player != null) {
            return new BungeePlayer<>(player);
        }

        return null;

    }

    @Override
    public Collection<ZulfProxyPlayer<ProxyServer>> getPlayers() {
        return getProxy().getPlayers().stream()
                .filter(Objects::nonNull)
                .map(BungeePlayer::new)
                .collect(Collectors.toList());
    }

    @Override
    public ZulfProxyServer<ProxyServer> getServer(String name) {

        ServerInfo bungeeServerInfo = getProxy().getServersCopy().get(name);

        if (bungeeServerInfo != null) {
            return new BungeeServer(getProxy().getServersCopy().get(name));
        }

        return null;

    }

    @Override
    public Map<String, ZulfServerInfo<ProxyServer>> getServersCopy() {

        HashMap<String, ZulfServerInfo<ProxyServer>>
                serverMap = new HashMap<>();

        for (ServerInfo bungeeInfo : getProxy().getServersCopy().values()) {
            serverMap.put(bungeeInfo.getName(), new ZulfServerInfo<>
                    (bungeeInfo.getSocketAddress(), getPlayers()));
        }

        return serverMap;
    }

    @Override
    public String getVersion() {
        return getDescription().getVersion();
    }

    @Override
    public File getPluginFolder() {
        return getDataFolder();
    }

    @Override
    public ProxyCommandSender<ProxyServer> getConsole() {
        return new BungeeConsole(getProxy());
    }

    @Override
    public String platformString() {
        return String.format("Bungeecord (%s)", getProxy().getVersion());
    }

    @Override
    public CheckUpdateTask<ProxyServer> getUpdater() {
        return updater;
    }

    public MainServer<ProxyServer> getServer() {
        return mainServer;
    }

    public BungeeTaskManager getTaskManager() {
        return bungeeTaskManager;
    }


}
