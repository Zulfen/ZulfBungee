package com.zulfen.zulfbungee.spigot.socket;

import com.zulfen.zulfbungee.spigot.ZulfBungeeSpigot;
import com.zulfen.zulfbungee.spigot.handlers.transport.ClientChannelCommHandler;

import java.net.SocketAddress;

public class ChannelConnection extends Connection {

    public ChannelConnection(ZulfBungeeSpigot pluginInstanceIn, SocketAddress socketAddressIn) {
        super(pluginInstanceIn, socketAddressIn);
        setClientCommHandler(new ClientChannelCommHandler(pluginInstanceIn));
    }



}
