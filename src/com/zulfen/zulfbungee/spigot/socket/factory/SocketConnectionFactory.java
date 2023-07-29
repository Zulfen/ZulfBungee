package com.zulfen.zulfbungee.spigot.socket.factory;

import com.zulfen.zulfbungee.spigot.interfaces.ConnectionFactory;
import com.zulfen.zulfbungee.spigot.managers.connections.SocketConnectionManager;
import com.zulfen.zulfbungee.spigot.socket.SocketConnection;

import java.io.IOException;
import java.net.Socket;

public class SocketConnectionFactory extends ConnectionFactory<SocketConnection, SocketConnectionManager> {

    private Socket socket;

    public SocketConnectionFactory(SocketConnectionManager connectionManagerIn) {
        super(connectionManagerIn);
    }

    public SocketConnectionFactory withSocket(Socket socketIn) {
        socket = socketIn;
        return this;
    }

    @Override
    public SocketConnection build() {
        try {
            SocketConnection socketConnection = new SocketConnection(connectionManager, socket);
            connectionManager.registerBefore();
            return socketConnection;
        } catch (IOException e) {
            throw new RuntimeException("Error creating new socket connection!", e);
        }
    }

}
