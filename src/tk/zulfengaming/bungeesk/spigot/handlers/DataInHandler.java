package tk.zulfengaming.bungeesk.spigot.handlers;

import tk.zulfengaming.bungeesk.spigot.socket.ClientConnection;

public class DataInHandler implements Runnable {

    private final ClientConnection connection;

    private boolean connected = false;

    public DataInHandler(ClientConnection connectionIn) {
        this.connection = connectionIn;

    }


    @Override
    public void run() {
        do {
            try {
                if (connected) {
                    Object dataIn =
                }
            }
        } while (connection.isRunning());
    }

    private Optional<Packet>
}
