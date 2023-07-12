package tk.zulfengaming.zulfbungee.universal.handlers.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.interfaces.ProxyServerConnection;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientInfo;

public class ConnectionName<P, T> extends PacketHandler<P, T> {

    public ConnectionName(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connection) {

        String givenName = (String) packetIn.getDataArray()[0];
        ClientInfo clientInfo = (ClientInfo) packetIn.getDataArray()[1];

        getMainServer().addActiveConnection(connection, givenName, clientInfo);

        return new Packet(PacketTypes.CONNECTION_NAME, true, true, givenName);

    }

}
