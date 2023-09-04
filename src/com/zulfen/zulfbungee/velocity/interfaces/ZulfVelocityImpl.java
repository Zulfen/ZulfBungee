package com.zulfen.zulfbungee.velocity.interfaces;

import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.ServerConnection;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import com.velocitypowered.api.proxy.server.RegisteredServer;
import com.velocitypowered.api.proxy.server.ServerInfo;
import com.zulfen.zulfbungee.universal.ZulfProxyImpl;
import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import com.zulfen.zulfbungee.universal.config.ProxyConfig;
import com.zulfen.zulfbungee.universal.interfaces.NativePlayerConverter;
import com.zulfen.zulfbungee.universal.managers.ProxyTaskManager;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import com.zulfen.zulfbungee.velocity.ZulfVelocityMain;
import com.zulfen.zulfbungee.velocity.command.VelocityConsole;
import com.zulfen.zulfbungee.velocity.config.VelocityConfig;
import com.zulfen.zulfbungee.velocity.objects.VelocityPlayer;
import com.zulfen.zulfbungee.velocity.objects.VelocityServer;
import com.zulfen.zulfbungee.velocity.task.VelocityTaskManager;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class ZulfVelocityImpl implements ZulfProxyImpl<ProxyServer, Player> {

    private final NativePlayerConverter<ProxyServer, Player> playerConverter = new NativePlayerConverter<ProxyServer, Player>() {
        @Override
        public Optional<ZulfProxyPlayer<ProxyServer, Player>> apply(Player nativePlayer) {
            Optional<ServerConnection> optionalServerConnection = nativePlayer.getCurrentServer();
            return optionalServerConnection.map(serverConnection -> new VelocityPlayer(nativePlayer,
                    new VelocityServer(serverConnection.getServer(), ZulfVelocityImpl.this), ZulfVelocityImpl.this));
        }
    };

    private final ProxyServer velocity;
    private final Logger logger;
    private final VelocityConfig config;

    private final VelocityConsole console;

    private final VelocityTaskManager taskManager;
    private final boolean isDebug;

    private final Path pluginFolderPath;
    private final String version;

    private final LegacyComponentSerializer legacyTextSerializer = LegacyComponentSerializer.builder()
            .character('&').
            hexCharacter('#')
            .hexColors()
            .build();

    public ZulfVelocityImpl(ProxyServer proxyServerIn, ZulfVelocityMain pluginInstance, Logger loggerIn, Path dataDirectoryIn, String versionIn) {
        this.velocity = proxyServerIn;
        this.logger = loggerIn;
        this.pluginFolderPath = dataDirectoryIn;
        this.config = new VelocityConfig(this);
        this.console = new VelocityConsole(this);
        this.taskManager = new VelocityTaskManager(this, pluginInstance);
        this.isDebug = config.getBoolean("debug");
        this.version = versionIn;
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
        return config;
    }

    @Override
    public Optional<ZulfProxyPlayer<ProxyServer, Player>> getPlayer(String nameIn) {
        Optional<Player> velocityPlayerOptional = velocity.getPlayer(nameIn);
        return velocityPlayerOptional.flatMap(playerConverter);
    }

    @Override
    public Optional<ZulfProxyPlayer<ProxyServer, Player>> getPlayer(UUID uuidIn) {
        Optional<Player> velocityPlayerOptional = velocity.getPlayer(uuidIn);
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
    public NativePlayerConverter<ProxyServer, Player> getPlayerConverter() {
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
    public void registerMessageChannel(String channelNameIn) {
        velocity.getChannelRegistrar().register(MinecraftChannelIdentifier.from(channelNameIn));
    }

    @Override
    public void unregisterMessageChannel(String channelNameIn) {
        velocity.getChannelRegistrar().unregister(MinecraftChannelIdentifier.from(channelNameIn));
    }

    @Override
    public void registerServer(String serverNameIn, SocketAddress addressIn) {
        ServerInfo serverInfo = new ServerInfo(serverNameIn, (InetSocketAddress) addressIn);
        velocity.registerServer(serverInfo);
    }

    @Override
    public void deRegisterServer(String serverNameIn) {
        Optional<RegisteredServer> serverOptional = velocity.getServer(serverNameIn);
        serverOptional.ifPresent(server -> velocity.unregisterServer(server.getServerInfo()));
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
        return version;
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

    public LegacyComponentSerializer getLegacyTextSerializer() {
        return legacyTextSerializer;
    }

    @Override
    public String platformString() {
        return String.format("Velocity &a(%s)", velocity.getVersion().getVersion());
    }

    @Override
    public boolean isDebug() {
        return isDebug;
    }

}
