package tk.zulfengaming.bungeesk.spigot.handlers;

import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.Callable;

public class SocketHandler implements Callable<Optional<Socket>> {

    private final ClientConnection connection;

    public SocketHandler(ClientConnection connectionIn) {
        this.connection = connectionIn;

    }

    @Override
    public Optional<Socket> call() {

        try {
            Socket socket = new Socket(connection.getServerAddress(), connection.getServerPort(), connection.getClientAddress(), connection.getClientPort());

            return Optional.of(socket);

        } catch (IOException ignored) {

        }

        return Optional.empty();

    }
}

