package tk.zulfengaming.zulfbungee.spigot.socket.packets;

import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.managers.ChannelConnectionManager;
import tk.zulfengaming.zulfbungee.spigot.managers.ConnectionManager;
import tk.zulfengaming.zulfbungee.spigot.socket.Connection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;

import java.util.Optional;

public class ConnectionName extends PacketHandler {

    public ConnectionName(Connection connectionIn) {
        super(connectionIn, true, PacketTypes.CONNECTION_NAME);

    }

    @Override
    public void handlePacket(Packet packetIn) {

        String name = (String) packetIn.getDataSingle();
        ConnectionManager connectionManager = getConnection().getPluginInstance().getConnectionManager();

        Optional<ClientServer> proxyServer = connectionManager.getProxyServer(name);
        proxyServer.ifPresent(connectionManager::setThisServer);

    }
}
