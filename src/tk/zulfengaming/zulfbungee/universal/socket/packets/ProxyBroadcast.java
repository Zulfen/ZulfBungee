package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ClientServerDataContainer;

public class ProxyBroadcast<P> extends PacketHandler<P> {

    public ProxyBroadcast(PacketHandlerManager<P> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection) {

        ClientServerDataContainer dataContainer = (ClientServerDataContainer) packetIn.getDataSingle();
        ClientServer[] servers = dataContainer.getServers();

        String message = (String) dataContainer.getData();

        if (servers.length > 0) {
            for (ClientServer server : servers) {
                getProxy().broadcast(message, server.getName());
            }
        } else {
            getProxy().broadcast(message);
        }

        return null;

    }
}