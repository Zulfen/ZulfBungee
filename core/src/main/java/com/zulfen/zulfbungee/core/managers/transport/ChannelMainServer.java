package com.zulfen.zulfbungee.core.managers.transport;

import com.zulfen.zulfbungee.core.ZulfProxyImpl;
import com.zulfen.zulfbungee.core.command.util.ChatColour;
import com.zulfen.zulfbungee.core.handlers.proxy.transport.ProxyChannelCommHandler;
import com.zulfen.zulfbungee.core.interfaces.MessageCallback;
import com.zulfen.zulfbungee.core.managers.MainServer;
import com.zulfen.zulfbungee.core.socket.transport.ChannelServerConnection;
import com.zulfen.zulfbungee.core.task.tasks.CheckUpdateTask;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelMainServer<P, T, C> extends MainServer<P, T, C> {

    private final ConcurrentHashMap<String, ChannelServerConnection<P, T, C>> channelConnections = new ConcurrentHashMap<>();

    public ChannelMainServer(ZulfProxyImpl<P, T, C> instanceIn, CheckUpdateTask<P, T, C> updateTaskIn) {
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
        ChannelServerConnection<P, T, C> connection = new ChannelServerConnection<>(this, callbackIn, addressIn);
        channelConnections.put(serverName, connection);
        createConnection(connection);
    }

    public void proccessPluginMessage(String serverNameIn, byte[] dataIn) {

        if (channelConnections.containsKey(serverNameIn)) {
            ProxyChannelCommHandler<P, T, C> channelCommHandler = channelConnections.get(serverNameIn).getProxyChannelCommHandler();
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
