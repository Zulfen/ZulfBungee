package com.zulfen.zulfbungee.spigot.socket;

import com.zulfen.zulfbungee.spigot.interfaces.transport.ClientChannelCommHandler;
import com.zulfen.zulfbungee.spigot.managers.connections.ChannelConnectionManager;
import com.zulfen.zulfbungee.spigot.socket.factory.ChannelConnectionFactory;
import org.bukkit.ChatColor;

import java.net.SocketAddress;

public class ClientChannelConnection extends Connection<ChannelConnectionFactory> {

    public ClientChannelConnection(ChannelConnectionManager connectionManager, SocketAddress socketAddressIn, int compressPackets) {
        super(connectionManager, socketAddressIn);
        setClientCommHandler(new ClientChannelCommHandler(connectionManager.getPluginInstance(), compressPackets));
    }

    @Override
    public void onRegister() {
        pluginInstance.logInfo(ChatColor.GREEN + "Established connection with proxy via plugin messaging channels!");
    }

}
