package tk.zulfengaming.zulfbungee.universal.socket;

import tk.zulfengaming.zulfbungee.universal.handlers.socket.ProxyChannelCommHandler;
import tk.zulfengaming.zulfbungee.universal.interfaces.MessageCallback;
import tk.zulfengaming.zulfbungee.universal.interfaces.ProxyServerConnection;

import java.net.SocketAddress;

public class ChannelServerConnection<P, T> extends ProxyServerConnection<P, T> {

    private final ProxyChannelCommHandler<P, T> proxyChannelCommHandler;

    public ChannelServerConnection(MainServer<P, T> mainServerIn, MessageCallback messageCallbackIn, SocketAddress socketAddressIn) {
        super(mainServerIn, socketAddressIn);
        this.proxyChannelCommHandler = new ProxyChannelCommHandler<>(pluginInstance, messageCallbackIn);
        setProxyCommHandler(proxyChannelCommHandler);
    }

    public ProxyChannelCommHandler<P, T> getProxyChannelCommHandler() {
        return proxyChannelCommHandler;
    }

}
