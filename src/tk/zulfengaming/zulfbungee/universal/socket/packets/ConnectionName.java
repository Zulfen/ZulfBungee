package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientInfo;

public class ConnectionName<P> extends PacketHandler<P> {

    public ConnectionName(PacketHandlerManager<P> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection) {

        String name = (String) packetIn.getDataArray()[0];
        ClientInfo clientInfo = (ClientInfo) packetIn.getDataArray()[1];

        getMainServer().addActiveConnection(connection, name, clientInfo);

        return new Packet(PacketTypes.CONNECTION_NAME, true, true, name);

    }

}
