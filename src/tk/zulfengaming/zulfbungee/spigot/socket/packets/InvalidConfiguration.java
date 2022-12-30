package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;

import java.net.SocketAddress;

public class InvalidConfiguration extends PacketHandler {

    public InvalidConfiguration(ClientConnection connectionIn) {
        super(connectionIn, PacketTypes.INVALID_CONFIGURATION);

    }

    // maybe will use now, kind of unused.

    @Override
    public void handlePacket(Packet packetIn, SocketAddress address) {

        getConnection().getPluginInstance().warning("The proxy disconnected you due to a configuration issue!");
        getConnection().getPluginInstance().warning("This client will not try and reconnect until this issue is fixed.");
        getConnection().getPluginInstance().warning("Check the proxy's console for more information.");

        getConnection().shutdown();

    }
}
