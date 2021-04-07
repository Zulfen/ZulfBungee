package tk.zulfengaming.bungeesk.spigot.handlers;

import tk.zulfengaming.bungeesk.spigot.interfaces.ClientListener;
import tk.zulfengaming.bungeesk.spigot.interfaces.ClientListenerManager;

import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.concurrent.Callable;

public class SocketHandler extends ClientListener implements Callable<Optional<Socket>> {

    private final ClientListenerManager clientListenerManager;

    public SocketHandler(ClientListenerManager clientListenerManagerIn) {
        super(clientListenerManagerIn);

        this.clientListenerManager = clientListenerManagerIn;


    }

    @Override
    public void onDisconnect() {

    }

    @Override
    public void onShutdown() {

    }

    @Override
    public Optional<Socket> call() throws InterruptedException {

        try {
            Socket socket = new Socket(clientListenerManager.getServerAddress(), clientListenerManager.getServerPort(), clientListenerManager.getClientAddress(), clientListenerManager.getClientPort());
            socket.setReuseAddress(true);

            return Optional.of(socket);

        } catch (IOException e) {
            Thread.sleep(2000);
        }

        return Optional.empty();

    }
}

