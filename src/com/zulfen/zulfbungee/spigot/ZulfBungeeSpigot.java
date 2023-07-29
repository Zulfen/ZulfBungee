package com.zulfen.zulfbungee.spigot;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAPIException;
import ch.njol.skript.SkriptAddon;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.zulfen.zulfbungee.spigot.event.EventListeners;
import com.zulfen.zulfbungee.spigot.managers.ConnectionManager;
import com.zulfen.zulfbungee.spigot.managers.TaskManager;
import com.zulfen.zulfbungee.spigot.managers.connections.ChannelConnectionManager;
import com.zulfen.zulfbungee.spigot.managers.connections.SocketConnectionManager;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class ZulfBungeeSpigot extends JavaPlugin {

    // static reference so we can access it via Skript
    private static ZulfBungeeSpigot plugin;
    private boolean debug = false;
    private String transportType;

    private TaskManager taskManager;
    private ConnectionManager<?> connectionManager;

    private ProtocolManager protocolManager;

    public void onEnable() {

        plugin = this;
        getServer().getPluginManager().registerEvents(new EventListeners(this), this);

        taskManager = new TaskManager(this);
        saveDefaultConfig();

        debug = getConfig().getBoolean("debug");

        protocolManager = ProtocolLibrary.getProtocolManager();

        try {

            transportType = getConfig().getString("transport-type");
            InetAddress serverAddress = InetAddress.getByName(getConfig().getString("server-host"));
            int serverPort = getConfig().getInt("server-port");

            if (transportType.equalsIgnoreCase("pluginmessage")) {
                connectionManager = new ChannelConnectionManager(this, serverAddress, serverPort);
            } else {
                InetAddress clientAddress = InetAddress.getByName(getConfig().getString("client-host"));
                int clientPort = getConfig().getInt("client-port");
                SocketConnectionManager socketConnectionManager = new SocketConnectionManager(this, clientAddress, clientPort, serverAddress, serverPort);
                connectionManager = socketConnectionManager;
                taskManager.newAsyncTask(socketConnectionManager);
            }


        } catch (UnknownHostException e) {

            error("Could not get the name of the host in the config!:");
            e.printStackTrace();

        }

        SkriptAddon addon = Skript.registerAddon(this);

        // Registers the addon
        try {

            addon.loadClasses("com.zulfen.zulfbungee.spigot", "elements");
            logInfo(ChatColor.GREEN + "The addon loaded successfully!");

        } catch (SkriptAPIException | IOException e) {
            error("The addon failed to register! :( please check the error!");
            e.printStackTrace();
        }

    }

    public void onDisable() {
        connectionManager.shutdown();
        taskManager.shutdown();
    }

    public void logDebug(String message) {
        if (debug) {
            getServer().getConsoleSender().sendMessage("[ZulfBungee] " + message);
        }
    }

    public void logInfo(String message) {
        getServer().getConsoleSender().sendMessage("[ZulfBungee] " + message);
    }

    public void error(String message) {
        getLogger().severe(message);
    }

    public void warning(String message) {
        getLogger().warning(message);
    }

    public TaskManager getTaskManager() {
        return taskManager;
    }

    public String getTransportType() {
        return transportType;
    }

    public ConnectionManager<?> getConnectionManager() {
        return connectionManager;
    }

    public ProtocolManager getProtocolManager() {
        return protocolManager;
    }


    public boolean isDebug() {
        return debug;
    }

    // static reference for Skript only
    public static ZulfBungeeSpigot getPlugin() {
        return plugin;
    }

}



