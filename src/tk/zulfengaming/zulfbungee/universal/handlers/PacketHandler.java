package tk.zulfengaming.zulfbungee.universal.handlers;

import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.interfaces.ProxyServerConnection;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;

public abstract class PacketHandler<P, T> {

    private final PacketHandlerManager<P, T> packetHandlerManager;

    public abstract Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connection);

    public MainServer<P, T> getMainServer() {
        return packetHandlerManager.getMainServer();
    }

    public ZulfBungeeProxy<P, T> getProxy() {
        return packetHandlerManager.getMainServer().getPluginInstance();
    }

    public PacketHandler(PacketHandlerManager<P, T> packetHandlerManagerIn) {
        this.packetHandlerManager = packetHandlerManagerIn;
    }

}
