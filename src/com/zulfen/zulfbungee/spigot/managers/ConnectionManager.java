package com.zulfen.zulfbungee.spigot.managers;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.variables.Variables;
import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.objects.PreparedNetworkVariable;
import com.zulfen.zulfbungee.spigot.socket.ClientConnection;
import com.zulfen.zulfbungee.spigot.tasks.GlobalScriptsTask;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientInfo;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.universal.socket.objects.client.HandshakePacket;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ScriptAction;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ScriptInfo;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.SerializedNetworkVariable;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.Value;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ConnectionManager<T> {

    protected final ZulfBungeeSpigot pluginInstance;
    protected final ConcurrentHashMap<String, ClientInfo> proxyServers = new ConcurrentHashMap<>();

    protected final CopyOnWriteArrayList<ClientConnection<T>> allConnections = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<SocketAddress> blockedConnections = new CopyOnWriteArrayList<>();

    protected final List<File> scriptFiles = Collections.synchronizedList(new ArrayList<>());

    protected final AtomicBoolean running = new AtomicBoolean(true);

    private final T connectionFactory;

    protected final TaskManager taskManager;

    // representation of this client as a server.
    private volatile ClientServer thisServer;

    public ConnectionManager(ZulfBungeeSpigot pluginIn, Class<T> connectionFactoryClass) {

        this.pluginInstance = pluginIn;
        this.taskManager = pluginInstance.getTaskManager();

        try {
            Constructor<?> constructor = connectionFactoryClass.getConstructor(getClass());
            Object possibleConnFactory = constructor.newInstance(this);
            connectionFactory = connectionFactoryClass.cast(possibleConnFactory);
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }

    protected abstract boolean sendDirectImpl(Packet packetIn);

    public synchronized boolean sendDirect(Packet packetIn) {
        if (pluginInstance.isEnabled()) {
            if (!allConnections.isEmpty() || packetIn instanceof HandshakePacket) {
                return sendDirectImpl(packetIn);
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    public abstract Optional<Packet> send(Packet packetIn);

    public abstract List<ClientPlayer> getPlayers(ClientServer[] serversIn);
    
    private Value[] serializeValues(Object[] delta) {
        return Stream.of(delta)
               .map(Classes::serialize)
               .filter(Objects::nonNull)
               .map(value -> new Value(value.type, value.data))
               .toArray(Value[]::new);
    }

    private Object[] deserializeValues(Value[] valuesIn) {
        return Stream.of(valuesIn)
                .map(value -> Classes.deserialize(value.type, value.data))
                .toArray(Object[]::new);
    }

    public Value[] threadSafeSerialize(Object[] delta) {

        Value[] values;

        if (pluginInstance.getServer().isPrimaryThread()) {
            values = serializeValues(delta);
        } else {
            values = taskManager.returnableMainThreadTask(() -> serializeValues(delta));
        }

        return values;

    }

    public Object[] threadSafeDeserialize(Value[] valuesIn) {

        Object[] dataOut;

        if (pluginInstance.getServer().isPrimaryThread()) {
            dataOut = deserializeValues(valuesIn);
        } else {
            dataOut = taskManager.returnableMainThreadTask(() -> deserializeValues(valuesIn));
        }

        return dataOut;

    }

    private PreparedNetworkVariable prepareNetworkVariable(SerializedNetworkVariable serializedVarIn, Event eventIn) {

        Value[] valueArray = serializedVarIn.getValueArray();
        Object[] dataOut = threadSafeDeserialize(valueArray);

        int length = valueArray.length;
        String varName = serializedVarIn.getName();

        if (length > 0) {
            return new PreparedNetworkVariable(varName, dataOut);
        } else {
            return null;
        }

    }

    public void modifyNetworkVariable(Object[] delta, Changer.ChangeMode mode, String variableNameIn) {
        Value[] values = new Value[0];
        if (mode != Changer.ChangeMode.DELETE) {
            values = threadSafeSerialize(delta);
        }
        SerializedNetworkVariable variableOut = new SerializedNetworkVariable(variableNameIn, mode.name(), values);
        send(new Packet(PacketTypes.NETWORK_VARIABLE_MODIFY, true, false, variableOut));
    }

    public synchronized Optional<PreparedNetworkVariable> requestNetworkVariable(String nameIn, Event eventIn) {

        Optional<Packet> send = send(new Packet(PacketTypes.NETWORK_VARIABLE_GET, true, false, nameIn));

        if (send.isPresent()) {

            Packet packet = send.get();
            if (packet.getDataArray().length > 0) {

                SerializedNetworkVariable serializedVar = (SerializedNetworkVariable) packet.getDataSingle();
                PreparedNetworkVariable preparedNetworkVariable = prepareNetworkVariable(serializedVar, eventIn);
                return Optional.ofNullable(preparedNetworkVariable);

            }

        }

        return Optional.empty();

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

    public void register(ClientConnection<T> connectionIn) {
        allConnections.add(connectionIn);
    }

    public void deRegister(ClientConnection<T> connectionIn) {
        allConnections.remove(connectionIn);
    }

    public void blockConnection(ClientConnection<?> connectionIn) {
        blockedConnections.add(connectionIn.getSocketAddress());
        connectionIn.destroy();
    }

    public boolean isBlocked(SocketAddress addressIn) {
        return blockedConnections.contains(addressIn);
    }

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

    // TODO: Re-do all of this at some point.
    public void processGlobalScript(@NotNull ScriptInfo infoIn, ClientConnection<?> connectionIn) {

        ScriptAction action = infoIn.getScriptAction();

        CommandSender sender = pluginInstance.getServer().getConsoleSender();

        if (infoIn.getSender() != null) {

            ClientPlayer playerIn = infoIn.getSender();
            Player playerOut = pluginInstance.getServer().getPlayer(playerIn.getUuid());

            if (playerOut != null) {
                sender = playerOut;
            }

        }

        getPluginInstance().getTaskManager().submitSupplier(new GlobalScriptsTask(pluginInstance, connectionIn, sender, infoIn))
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

            for (ClientConnection<T> connection : allConnections) {
                connection.destroy();
            }

            for (File scriptFile : scriptFiles) {
                boolean delete = scriptFile.delete();
                if (!delete) {
                    pluginInstance.warning(String.format("Script %s could not be deleted. Does it exist?", scriptFile.getName()));
                }
            }

        }

    }

    public T createNewConnection() {
        return connectionFactory;
    }

    public AtomicBoolean isRunning() {
        return running;
    }

    public ZulfBungeeSpigot getPluginInstance() {
        return pluginInstance;
    }

}
