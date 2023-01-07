package tk.zulfengaming.zulfbungee.bungeecord.socket;

import net.md_5.bungee.api.ProxyServer;
import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class BungeeMainServer extends MainServer<ProxyServer> {

    public BungeeMainServer(int port, InetAddress address, ZulfBungeeProxy<ProxyServer> instanceIn) {
        super(port, address, instanceIn);
    }

    @Override
    protected BaseServerConnection<ProxyServer> newConnection(Socket socketIn) throws IOException {
        return new BungeeServerConnection(this, socketIn);
    }
}
