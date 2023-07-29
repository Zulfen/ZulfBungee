package com.zulfen.zulfbungee.spigot.socket;

import com.zulfen.zulfbungee.spigot.handlers.transport.ClientSocketCommHandler;
import com.zulfen.zulfbungee.spigot.managers.connections.SocketConnectionManager;
import com.zulfen.zulfbungee.spigot.socket.factory.SocketConnectionFactory;
import org.bukkit.ChatColor;

import java.io.IOException;
import java.net.Socket;

public class SocketConnection extends Connection<SocketConnectionFactory> {

    private final SocketConnectionManager socketConnectionManager;

    public SocketConnection(SocketConnectionManager connectionManagerIn, Socket socketIn) throws IOException {
        super(connectionManagerIn, socketIn.getRemoteSocketAddress());
        setClientCommHandler(new ClientSocketCommHandler(pluginInstance, socketIn));
        this.socketConnectionManager = connectionManagerIn;
    }

    @Override
    public void onRegister() {
        socketConnectionManager.releaseConnectionBarrier();
        pluginInstance.logInfo(String.format("%sConnection established with proxy via socket! (%s)", ChatColor.GREEN, socketAddress));
    }

}