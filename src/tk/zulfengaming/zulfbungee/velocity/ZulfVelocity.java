package tk.zulfengaming.zulfbungee.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;
import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.config.ProxyConfig;
import tk.zulfengaming.zulfbungee.universal.interfaces.MessageCallback;
import tk.zulfengaming.zulfbungee.universal.interfaces.NativePlayerConverter;
import tk.zulfengaming.zulfbungee.universal.managers.CommandHandlerManager;
import tk.zulfengaming.zulfbungee.universal.managers.ProxyTaskManager;
import tk.zulfengaming.zulfbungee.universal.socket.ChannelMainServer;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.SocketMainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import tk.zulfengaming.zulfbungee.universal.task.tasks.CheckUpdateTask;
import tk.zulfengaming.zulfbungee.velocity.command.VelocityCommand;
import tk.zulfengaming.zulfbungee.velocity.command.VelocityConsole;
import tk.zulfengaming.zulfbungee.velocity.config.VelocityConfig;
import tk.zulfengaming.zulfbungee.velocity.event.VelocityEvents;
import tk.zulfengaming.zulfbungee.velocity.objects.VelocityPlayer;
import tk.zulfengaming.zulfbungee.velocity.objects.VelocityServer;
import tk.zulfengaming.zulfbungee.velocity.task.VelocityTaskManager;

import javax.inject.Inject;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

@Plugin(id = "zulfbungee", name = "zulfbungee", version = ZulfVelocity.VERSION, url = "https://github.com/Zulfen/ZulfBungee",
description = "A Skript addon which adds proxy integration.", authors = {"zulfen"})
public class ZulfVelocity implements ZulfBungeeProxy<ProxyServer, Player> {

    protected static final String VERSION = "0.9.8-pre2";

    private final ProxyServer velocity;
    private final VelocityConfig pluginConfig;
    private MainServer<ProxyServer, Player> mainServer;

    private final Logger logger;

    private final Path pluginFolderPath;

    private final boolean isDebug;

    private final CheckUpdateTask<ProxyServer, Player> updater;

    private final VelocityTaskManager taskManager;

    private final VelocityConsole console;

    private final String transportType;

    private final LegacyComponentSerializer legacyTextSerializer = LegacyComponentSerializer.builder()
            .character('&').
            hexCharacter('#')
            .hexColors()
            .build();

    private final NativePlayerConverter<Player, ProxyServer> playerConverter = new NativePlayerConverter<Player, ProxyServer>() {
        @Override
        public Optional<ZulfProxyPlayer<ProxyServer, Player>> apply(Player nativePlayer) {
            Optional<ServerConnection> optionalServerConnection = nativePlayer.getCurrentServer();
            return optionalServerConnection.map(serverConnection -> new VelocityPlayer(nativePlayer,
                    new VelocityServer(serverConnection.getServer(), ZulfVelocity.this), ZulfVelocity.this));
        }
    };

    @Inject
    public ZulfVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {

        this.velocity = server;
        this.console = new VelocityConsole(this);
        this.logger = logger;
        this.pluginFolderPath = dataDirectory;
        this.pluginConfig = new VelocityConfig(this);

        this.taskManager = new VelocityTaskManager(this);

        this.isDebug = pluginConfig.getBoolean("debug");
        this.transportType = pluginConfig.getString("transport-type");
        this.updater = new CheckUpdateTask<>(this);

    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {

        try {

            if (transportType.equalsIgnoreCase("pluginmessage")) {
                mainServer = new ChannelMainServer<>(this);
            } else if (transportType.equalsIgnoreCase("socket")) {
                SocketMainServer<ProxyServer, Player> socketMainServer = new SocketMainServer<>(pluginConfig.getInt("port"),
                        InetAddress.getByName(pluginConfig.getString("host")), this);
                taskManager.newTask(socketMainServer);
                mainServer = socketMainServer;
            } else {
                throw new RuntimeException("Invalid transport type chosen! Please check the config.");
            }

            velocity.getEventManager().register(this, new VelocityEvents(mainServer));
            velocity.getCommandManager().register("zulfbungee", new VelocityCommand(new CommandHandlerManager<>(mainServer)));

        } catch (UnknownHostException e) {
            error("Could not start the server! (velocity)");
            e.printStackTrace();
        }


    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        try {
            mainServer.end();
            taskManager.shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void logDebug(String messageIn) {
        if (isDebug) logInfo(messageIn);
    }

    @Override
    public void logInfo(String messageIn) {
        logger.info(messageIn);
    }

    @Override
    public void error(String messageIn) {
        logger.error(messageIn);
    }

    @Override
    public void warning(String messageIn) {
        logger.warn(messageIn);
    }

    @Override
    public ProxyTaskManager getTaskManager() {
        return taskManager;
    }

    @Override
    public ProxyConfig<ProxyServer, Player> getConfig() {
        return pluginConfig;
    }

    @Override
    public Optional<ZulfProxyPlayer<ProxyServer, Player>> getPlayer(String nameIn) {
        Optional<Player> velocityPlayerOptional = velocity.getPlayer(nameIn);
        return velocityPlayerOptional.flatMap(playerConverter);
    }

    @Override
    public List<ZulfProxyPlayer<ProxyServer, Player>> getAllPlayers() {
        return velocity.getAllPlayers().stream()
                .map(playerConverter)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ZulfProxyServer<ProxyServer, Player>> getServer(String serverNameIn) {
        Optional<RegisteredServer> server = velocity.getServer(serverNameIn);
        return server.map(registeredServer -> new VelocityServer(registeredServer, this));
    }

    @Override
    public NativePlayerConverter<Player, ProxyServer> getPlayerConverter() {
        return playerConverter;
    }

    @Override
    public Map<String, ZulfProxyServer<ProxyServer, Player>> getServersCopy() {

        HashMap<String, ZulfProxyServer<ProxyServer, Player>> serversMap = new HashMap<>();

        for (RegisteredServer server : velocity.getAllServers()) {
            serversMap.put(server.getServerInfo().getName(), new VelocityServer(server, this));
        }

        return serversMap;

    }

    @Override
    public Optional<MessageCallback> getMessagingCallback(String channelNameIn, String serverNameIn) {
        Optional<RegisteredServer> server = velocity.getServer(serverNameIn);
        return server.map(registeredServer ->
                dataIn -> registeredServer.sendPluginMessage(MinecraftChannelIdentifier.from(channelNameIn), dataIn));

    }

    @Override
    public void registerMessageChannel(String channelNameIn) {
        velocity.getChannelRegistrar().register(MinecraftChannelIdentifier.from(channelNameIn));
    }

    @Override
    public void unregisterMessageChannel(String channelNameIn) {
        velocity.getChannelRegistrar().unregister(MinecraftChannelIdentifier.from(channelNameIn));
    }

    @Override
    public void broadcast(String messageIn) {
        console.sendMessage(messageIn);
        for (RegisteredServer registeredServer : velocity.getAllServers()) {
            registeredServer.sendMessage(legacyTextSerializer.deserialize(messageIn));
        }
    }

    @Override
    public void broadcast(String messageIn, ZulfProxyServer<ProxyServer, Player> serverIn) {
        Optional<RegisteredServer> serverOptional = velocity.getServer(serverIn.getName());
        if (serverOptional.isPresent()) {
            Collection<Player> players = serverOptional.get().getPlayersConnected();
            players.forEach(player -> player.sendMessage(legacyTextSerializer.deserialize(messageIn)));
        }
    }


    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public Path getPluginFolder() {
        return pluginFolderPath;
    }

    @Override
    public ProxyCommandSender<ProxyServer, Player> getConsole() {
        return console;
    }

    @Override
    public ProxyServer getPlatform() {
        return velocity;
    }

    @Override
    public String platformString() {
        return String.format("Velocity &a(%s)", velocity.getVersion().getVersion());
    }

    @Override
    public String getTransportType() {
        return transportType;
    }

    @Override
    public CheckUpdateTask<ProxyServer, Player> getUpdater() {
        return updater;
    }

    @Override
    public boolean isDebug() {
        return isDebug;
    }

    public LegacyComponentSerializer getLegacyTextSerializer() {
        return legacyTextSerializer;
    }

}
