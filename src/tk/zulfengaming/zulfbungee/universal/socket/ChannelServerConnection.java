package tk.zulfengaming.zulfbungee.universal.socket;

import tk.zulfengaming.zulfbungee.universal.handlers.socket.ProxyChannelCommHandler;
import tk.zulfengaming.zulfbungee.universal.interfaces.MessageCallback;
import tk.zulfengaming.zulfbungee.universal.interfaces.ProxyServerConnection;

import java.net.SocketAddress;

public class ChannelServerConnection<P, T> extends ProxyServerConnection<P, T> {

    public ChannelServerConnection(MainServer<P, T> mainServerIn, MessageCallback messageCallbackIn, SocketAddress socketAddressIn) {
        super(mainServerIn, new ProxyChannelCommHandler<>(mainServerIn.getPluginInstance(), messageCallbackIn), socketAddressIn);
    }


}
