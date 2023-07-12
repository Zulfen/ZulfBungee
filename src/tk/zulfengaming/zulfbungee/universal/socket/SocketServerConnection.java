package tk.zulfengaming.zulfbungee.universal.socket;

import tk.zulfengaming.zulfbungee.universal.handlers.socket.ProxySocketCommHandler;
import tk.zulfengaming.zulfbungee.universal.interfaces.ProxyServerConnection;

import java.io.IOException;
import java.net.Socket;

public class SocketServerConnection<P, T> extends ProxyServerConnection<P, T> {

    public SocketServerConnection(MainServer<P, T> mainServerIn, Socket socketIn) throws IOException {
        super(mainServerIn, new ProxySocketCommHandler<>(mainServerIn.getPluginInstance(), socketIn), socketIn.getRemoteSocketAddress());
    }

}
