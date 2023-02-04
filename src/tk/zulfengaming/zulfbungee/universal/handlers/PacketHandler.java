package tk.zulfengaming.zulfbungee.universal.handlers;

import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;

public abstract class PacketHandler<P> {

    private final PacketHandlerManager<P> packetHandlerManager;

    public abstract Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection);

    public MainServer<P> getMainServer() {
        return packetHandlerManager.getMainServer();
    }

    public ZulfBungeeProxy<P> getProxy() {
        return packetHandlerManager.getMainServer().getPluginInstance();
    }

    public PacketHandler(PacketHandlerManager<P> packetHandlerManagerIn) {
        this.packetHandlerManager = packetHandlerManagerIn;
    }

}
