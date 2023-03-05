package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.Broadcast;

public class ProxyBroadcast<P> extends PacketHandler<P> {

    public ProxyBroadcast(PacketHandlerManager<P> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection) {

        Broadcast broadcast = (Broadcast) packetIn.getDataSingle();
        ClientServer[] servers = broadcast.getServers();

        if (servers.length > 0) {
            for (ClientServer server : servers) {
                getProxy().broadcast(broadcast.getMessage(), server.getName());
            }
        } else {
            getProxy().broadcast(broadcast.getMessage());
        }

        return null;

    }
}