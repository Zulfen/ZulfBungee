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
    public Optional<Socket> call() throws InterruptedException {

        try {
            Socket socket = new Socket(connection.getServerAddress(), connection.getServerPort(), connection.getClientAddress(), connection.getClientPort());
            socket.setReuseAddress(true);

            return Optional.of(socket);

        } catch (IOException e) {
            Thread.sleep(2000);
        }

        return Optional.empty();

    }
}

