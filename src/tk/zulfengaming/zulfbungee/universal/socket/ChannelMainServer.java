package tk.zulfengaming.zulfbungee.universal.socket;

import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.handlers.socket.ProxyChannelCommHandler;
import tk.zulfengaming.zulfbungee.universal.interfaces.MessageCallback;
import tk.zulfengaming.zulfbungee.universal.interfaces.ProxyCommHandler;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelMainServer<P, T> extends MainServer<P, T> {

    private final ConcurrentHashMap<String, ChannelServerConnection<P, T>> channelConnections = new ConcurrentHashMap<>();

    public ChannelMainServer(ZulfBungeeProxy<P, T> instanceIn) {

        super(instanceIn);
        pluginInstance.registerMessageChannel("zproxy:channel");

        for (ZulfProxyServer<P, T> server : pluginInstance.getServersCopy().values()) {
            acceptMessagingConnection(server.getSocketAddress(), server.getName(), pluginInstance.getMessagingCallback(
                    "zproxy:channel", server));
        }

    }

    @Override
    public void end() throws IOException {
        pluginInstance.unregisterMessageChannel("zproxy:channel");
        super.end();
    }

    private void acceptMessagingConnection(SocketAddress addressIn, String serverName, MessageCallback callbackIn) {
        ChannelServerConnection<P, T> connection = new ChannelServerConnection<>(this, callbackIn, addressIn);
        channelConnections.put(serverName, connection);
        startConnection(connection);
    }

    public void proccessPluginMessage(String serverNameIn, byte[] dataIn) {

        if (channelConnections.containsKey(serverNameIn)) {
            ProxyCommHandler<P, T> getHandler = channelConnections.get(serverNameIn).getProxyCommHandler();
            if (getHandler instanceof ProxyChannelCommHandler) {
                ProxyChannelCommHandler<P, T> channelCommHandler = (ProxyChannelCommHandler<P, T>) getHandler;
                channelCommHandler.provideBytes(dataIn);
            }
        }

    }

}
