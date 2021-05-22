package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.io.IOException;
import java.net.SocketAddress;

public class InvalidConfiguration extends PacketHandler {

    public InvalidConfiguration(ClientConnection connection) {
        super(connection, PacketTypes.INVALID_CONFIGURATION);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        getConnection().getPluginInstance().warning("The proxy disconnected you due to a configuration issue!");
        getConnection().getPluginInstance().warning("This client will not try and reconnect until this issue is fixed.");
        getConnection().getPluginInstance().warning("Check the proxy's console for more information.");

        try {
            getConnection().shutdown();

        } catch (IOException e) {
            getConnection().getPluginInstance().error("Error shutting down the connection!");

            e.printStackTrace();
        }

        return null;

    }
}
