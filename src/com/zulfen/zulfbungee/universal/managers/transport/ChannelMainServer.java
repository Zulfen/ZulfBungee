package com.zulfen.zulfbungee.universal.managers.transport;

import com.zulfen.zulfbungee.universal.ZulfProxyImpl;
import com.zulfen.zulfbungee.universal.command.util.ChatColour;
import com.zulfen.zulfbungee.universal.handlers.transport.ProxyChannelCommHandler;
import com.zulfen.zulfbungee.universal.interfaces.MessageCallback;
import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.universal.socket.transport.ChannelServerConnection;
import com.zulfen.zulfbungee.universal.task.tasks.CheckUpdateTask;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelMainServer<P, T> extends MainServer<P, T> {

    private final ConcurrentHashMap<String, ChannelServerConnection<P, T>> channelConnections = new ConcurrentHashMap<>();

    public ChannelMainServer(ZulfProxyImpl<P, T> instanceIn, CheckUpdateTask<P, T> updateTaskIn) {
        super(instanceIn, updateTaskIn);
        pluginInstance.registerMessageChannel("zproxy:channel");
        pluginInstance.logInfo(ChatColour.GREEN + "Waiting for a player to join...");
    }

    @Override
    public void end() throws IOException {
        pluginInstance.unregisterMessageChannel("zproxy:channel");
        super.end();
    }

    public void acceptMessagingConnection(SocketAddress addressIn, String serverName, MessageCallback callbackIn) {
        ChannelServerConnection<P, T> connection = new ChannelServerConnection<>(this, callbackIn, addressIn);
        channelConnections.put(serverName, connection);
        createConnection(connection);
    }

    public void proccessPluginMessage(String serverNameIn, byte[] dataIn) {

        if (channelConnections.containsKey(serverNameIn)) {
            ProxyChannelCommHandler<P, T> channelCommHandler = channelConnections.get(serverNameIn).getProxyChannelCommHandler();
            channelCommHandler.provideBytes(dataIn);
        }

    }

    public boolean isChannelConnectionActive(String nameIn) {
        return channelConnections.containsKey(nameIn);
    }

    @Override
    public void removeServerConnection(String name, SocketAddress address) {

        channelConnections.remove(name);

        if (channelConnections.isEmpty()) {
            pluginInstance.logInfo(ChatColour.GREEN + "Waiting for a player to join...");
        }

        super.removeServerConnection(name, address);

    }

}
