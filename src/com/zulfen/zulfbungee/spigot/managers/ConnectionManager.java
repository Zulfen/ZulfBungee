package com.zulfen.zulfbungee.spigot.managers;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.registrations.Classes;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.tasks.GlobalScriptsTask;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.NetworkVariable;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ScriptAction;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ScriptInfo;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.Value;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.zulfen.zulfbungee.spigot.socket.Connection;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientInfo;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ConnectionManager {

    protected final ZulfBungeeSpigot pluginInstance;
    protected final ConcurrentHashMap<String, ClientInfo> proxyServers = new ConcurrentHashMap<>();

    protected final List<File> scriptFiles = Collections.synchronizedList(new ArrayList<>());

    protected final AtomicBoolean running = new AtomicBoolean(true);

    protected final TaskManager taskManager;

    // representation of this client as a server.
    private ClientServer thisServer;

    public ConnectionManager(ZulfBungeeSpigot pluginIn) {
        this.pluginInstance = pluginIn;
        this.taskManager = pluginInstance.getTaskManager();
    }

    public abstract void sendDirect(Packet packetIn);

    // returns the first packet it gets from any of the connections. keep this in mind.
    public abstract Optional<Packet> send(Packet packetIn);

    public abstract List<ClientPlayer> getPlayers(ClientServer[] serversIn);

    public Optional<NetworkVariable> requestNetworkVariable(String nameIn) {

        Optional<Packet> send = send(new Packet(PacketTypes.NETWORK_VARIABLE_GET, true, false, nameIn));

        if (send.isPresent()) {
            Packet packet = send.get();
            if (packet.getDataArray().length > 0) {
                return Optional.of((NetworkVariable) packet.getDataSingle());
            }
        }

        return Optional.empty();

    }

    public void modifyNetworkVariable(Object[] delta, Changer.ChangeMode mode, String variableNameIn) {

        Value[] values = new Value[0];

        if (mode != Changer.ChangeMode.DELETE) {
            values = Stream.of(delta)
                    .map(Classes::serialize)
                    .filter(Objects::nonNull)
                    .map(value -> new Value(value.type, value.data))
                    .toArray(Value[]::new);
        }

        NetworkVariable variableOut = new NetworkVariable(variableNameIn, mode.name(), values);
        sendDirect(new Packet(PacketTypes.NETWORK_VARIABLE_MODIFY, true, false, variableOut));

    }

    public boolean proxyServerOnline(String serverNameIn) {
        return proxyServers.containsKey(serverNameIn);
    }

    public void setProxyServers(Map<String, ClientInfo> serversIn) {

        boolean changed = proxyServers.keySet().retainAll(serversIn.keySet());

        if (!changed) {
            proxyServers.putAll(serversIn);
        }

    }

    public abstract void blockConnection(Connection connectionIn);

    public List<ClientServer> getAllProxyServers() {
        return proxyServers.entrySet().stream()
                .map(server -> new ClientServer(server.getKey(), server.getValue()))
                .collect(Collectors.toList());
    }

    public Optional<ClientServer> getProxyServer(String nameIn) {
        ClientInfo zulfServerInfo = proxyServers.get(nameIn);
        return zulfServerInfo != null ? Optional.of(new ClientServer(nameIn, zulfServerInfo)) : Optional.empty();
    }

    public void setThisServer(ClientServer thisServer) {
        this.thisServer = thisServer;
    }

    // returns the first assigned connection as of now.
    public Optional<ClientServer> getAsServer() {
        return Optional.ofNullable(thisServer);
    }

    // TODO: Re-do all of this at some point to buffer scripts instead of sending them in one large packet.
    public void processGlobalScript(@NotNull ScriptInfo infoIn) {

        ScriptAction action = infoIn.getScriptAction();

        CommandSender sender = pluginInstance.getServer().getConsoleSender();

        if (infoIn.getSender() != null) {

            ClientPlayer playerIn = infoIn.getSender();
            Player playerOut = pluginInstance.getServer().getPlayer(playerIn.getUuid());

            if (playerOut != null) {
                sender = playerOut;
            }

        }

        getPluginInstance().getTaskManager().submitSupplier(new GlobalScriptsTask(pluginInstance, infoIn.getScriptName(), action, sender, infoIn.getScriptData()))
                .thenAccept(file -> {
                    switch (action) {
                        case RELOAD:
                            if (!scriptFiles.contains(file)) {
                                scriptFiles.add(file);
                            }
                            break;
                        case DELETE:
                            scriptFiles.remove(file);
                            break;
                    }
                });

    }


    public void shutdown() {

        if (running.compareAndSet(true, false)) {

            for (File scriptFile : scriptFiles) {
                boolean delete = scriptFile.delete();
                if (!delete) {
                    pluginInstance.warning(String.format("Script %s could not be deleted. Does it exist?", scriptFile.getName()));
                }
            }

        }

    }

    public AtomicBoolean isRunning() {
        return running;
    }

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
    }

}
