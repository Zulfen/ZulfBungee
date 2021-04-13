package tk.zulfengaming.bungeesk.bungeecord.handlers;

import tk.zulfengaming.bungeesk.bungeecord.socket.Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.Optional;
import java.util.concurrent.Callable;

public class SocketHandler implements Callable<Optional<ServerSocket>> {

    private final Server server;

    public SocketHandler(Server serverIn) {
        this.server = serverIn;

    }

    @Override
    public Optional<ServerSocket> call() throws InterruptedException {

        try {
            ServerSocket socket = new ServerSocket(server.getPort(), 50, server.getHostAddress());
            socket.setReuseAddress(true);

            return Optional.of(socket);

        } catch (IOException e) {
            Thread.sleep(2000);

        }

        return Optional.empty();

    }
}

