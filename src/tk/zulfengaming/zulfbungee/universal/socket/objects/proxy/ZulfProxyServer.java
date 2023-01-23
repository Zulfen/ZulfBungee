package tk.zulfengaming.zulfbungee.universal.socket.objects.proxy;


import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;

public interface ZulfProxyServer<P> {
    String getName();
    ZulfServerInfo<P> getServerInfo();
    BaseServerConnection<P> getConnection();
}
