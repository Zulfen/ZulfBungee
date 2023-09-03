package com.zulfen.zulfbungee.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.zulfen.zulfbungee.universal.managers.CommandHandlerManager;
import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.velocity.command.VelocityCommand;
import com.zulfen.zulfbungee.velocity.event.VelocityEvents;
import com.zulfen.zulfbungee.velocity.interfaces.ZulfVelocityPlugin;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

@Plugin(id = "zulfbungee", name = "zulfbungee", version = ZulfVelocityMain.VERSION, url = "https://github.com/Zulfen/ZulfBungee",
        description = "A Skript addon which adds proxy integration.", authors = {"zulfen"})
public class ZulfVelocityMain {

    protected final static String VERSION = "0.9.9-pre4";
    private final ProxyServer velocity;
    private final ZulfVelocityPlugin plugin;

    private final MainServer<ProxyServer, Player> mainServer;

    @Inject
    public ZulfVelocityMain(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.velocity = server;
        this.plugin = new ZulfVelocityPlugin(velocity, logger, dataDirectory, VERSION);
        this.mainServer = plugin.getMainServer();
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        velocity.getEventManager().register(this, new VelocityEvents(mainServer));
        velocity.getCommandManager().register("zulfbungee", new VelocityCommand(new CommandHandlerManager<>(mainServer)));
    }

    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        try {
            mainServer.end();
            plugin.getTaskManager().shutdown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
