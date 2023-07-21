package tk.zulfengaming.zulfbungee.spigot.socket;

import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.spigot.handlers.ClientChannelCommHandler;

import java.net.SocketAddress;

public class ChannelConnection extends Connection {

    public ChannelConnection(ZulfBungeeSpigot pluginInstanceIn, SocketAddress socketAddressIn) {
        super(pluginInstanceIn, socketAddressIn);
        setClientCommHandler(new ClientChannelCommHandler(pluginInstanceIn));
    }



}
