package tk.zulfengaming.zulfbungee.universal.interfaces;

import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;

public abstract class PacketHandler {

    private final PacketTypes[] types;

    private final MainServer mainServer;

    public abstract Packet handlePacket(Packet packetIn, BaseServerConnection connection);

    public PacketTypes[] getTypes() {
        return types;
    }

    public MainServer getMainServer() {
        return mainServer;
    }

    public ZulfBungeeProxy getProxy() {
        return mainServer.getPluginInstance();
    }

    public PacketHandler(MainServer mainServerIn, PacketTypes... types) {
        this.mainServer = mainServerIn;
        this.types = types;

    }

}
