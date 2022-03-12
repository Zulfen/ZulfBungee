package tk.zulfengaming.zulfbungee.bungeecord.interfaces;

import net.md_5.bungee.api.ProxyServer;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

public abstract class PacketHandler {

    private final PacketTypes[] types;

    private final Server mainServer;

    public abstract Packet handlePacket(Packet packetIn, BaseServerConnection connection);

    public PacketTypes[] getTypes() {
        return types;
    }

    public Server getMainServer() {
        return mainServer;
    }

    public ProxyServer getProxy() {
        return mainServer.getPluginInstance().getProxy();
    }

    public PacketHandler(Server serverIn, PacketTypes... types) {
        this.mainServer = serverIn;
        this.types = types;

    }

}
