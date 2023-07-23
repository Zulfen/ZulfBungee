package tk.zulfengaming.zulfbungee.universal.socket.transport;

import tk.zulfengaming.zulfbungee.universal.handlers.transport.ProxySocketCommHandler;
import tk.zulfengaming.zulfbungee.universal.socket.ProxyServerConnection;
import tk.zulfengaming.zulfbungee.universal.managers.MainServer;

import java.io.IOException;
import java.net.Socket;

public class SocketServerConnection<P, T> extends ProxyServerConnection<P, T> {

    public SocketServerConnection(MainServer<P, T> mainServerIn, Socket socketIn) throws IOException {
        super(mainServerIn, socketIn.getRemoteSocketAddress());
        setProxyCommHandler(new ProxySocketCommHandler<>(mainServerIn.getPluginInstance(), socketIn));
    }

}
