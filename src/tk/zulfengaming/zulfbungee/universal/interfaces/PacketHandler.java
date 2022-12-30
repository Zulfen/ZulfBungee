package tk.zulfengaming.zulfbungee.universal.interfaces;

import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;

public abstract class PacketHandler<P> {

    private final PacketTypes[] types;

    private final MainServer<P> mainServer;

    public abstract Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection);

    public PacketTypes[] getTypes() {
        return types;
    }

    public MainServer<P> getMainServer() {
        return mainServer;
    }

    public ZulfBungeeProxy<P> getProxy() {
        return mainServer.getPluginInstance();
    }

    public PacketHandler(MainServer<P> mainServerIn, PacketTypes... types) {
        this.mainServer = mainServerIn;
        this.types = types;

    }

}
