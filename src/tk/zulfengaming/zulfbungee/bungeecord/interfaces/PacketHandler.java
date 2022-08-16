package tk.zulfengaming.zulfbungee.bungeecord.interfaces;

import net.md_5.bungee.api.ProxyServer;
import tk.zulfengaming.zulfbungee.bungeecord.socket.MainServer;
import tk.zulfengaming.zulfbungee.bungeecord.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

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

    public ProxyServer getProxy() {
        return mainServer.getPluginInstance().getProxy();
    }

    public PacketHandler(MainServer mainServerIn, PacketTypes... types) {
        this.mainServer = mainServerIn;
        this.types = types;

    }

}
