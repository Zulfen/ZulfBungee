package com.zulfen.zulfbungee.spigot.handlers.transport;

import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.socket.factory.SocketConnectionFactory;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.spigot.interfaces.transport.ClientCommHandler;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Optional;

public class ClientSocketCommHandler extends ClientCommHandler<SocketConnectionFactory> {

    private final Socket socket;

    private final ObjectOutputStream outputStream;
    private final ObjectInputStream inputStream;

    public ClientSocketCommHandler(ZulfBungeeSpigot instanceIn, Socket socketIn) throws IOException {
        super(instanceIn);
        this.socket = socketIn;
        this.outputStream = new ObjectOutputStream(socket.getOutputStream());
        this.inputStream = new ObjectInputStream(socket.getInputStream());
        awaitProperConnection.countDown();
    }

    @Override
    public Optional<Packet> readPacket() {

        try {
            Object readObject = inputStream.readObject();
            if (readObject instanceof Packet) {
                return Optional.of((Packet) readObject);
            }
        } catch (IOException | ClassNotFoundException e) {
            if (pluginInstance.isDebug()) {
                e.printStackTrace();
            }
            destroy();
        }

        return Optional.empty();

    }

    @Override
    public synchronized void writePacket(Packet toWrite) {
        try {
            outputStream.writeObject(toWrite);
            outputStream.flush();
        } catch (IOException e) {
            if (pluginInstance.isDebug()) {
                e.printStackTrace();
            }
            destroy();
        }
    }

    @Override
    protected void freeResources() {
        try {
            socket.close();
        } catch (IOException e) {
            pluginInstance.error("Error closing socket on connection " + connection.getAddress());
            e.printStackTrace();
        }
    }

}
