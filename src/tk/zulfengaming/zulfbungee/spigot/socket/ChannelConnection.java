package tk.zulfengaming.zulfbungee.spigot.socket;

import tk.zulfengaming.zulfbungee.spigot.handlers.ClientChannelCommHandler;
import tk.zulfengaming.zulfbungee.spigot.managers.ConnectionManager;

import java.net.SocketAddress;

public class ChannelConnection extends Connection {
    public ChannelConnection(ConnectionManager connectionManagerIn, SocketAddress socketAddressIn) {
        super(connectionManagerIn, new ClientChannelCommHandler(connectionManagerIn.getPluginInstance()), socketAddressIn);
    }
}
