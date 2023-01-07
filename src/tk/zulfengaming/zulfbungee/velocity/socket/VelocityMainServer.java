package tk.zulfengaming.zulfbungee.velocity.socket;

import com.velocitypowered.api.proxy.ProxyServer;
import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public class VelocityMainServer extends MainServer<ProxyServer> {

    public VelocityMainServer(int port, InetAddress address, ZulfBungeeProxy<ProxyServer> instanceIn) {
        super(port, address, instanceIn);
    }

    @Override
    protected BaseServerConnection<ProxyServer> newConnection(Socket socketIn) throws IOException {
        return new VelocityServerConnection(this, socketIn);
    }

}
