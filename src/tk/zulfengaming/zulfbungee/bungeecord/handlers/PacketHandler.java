package tk.zulfengaming.zulfbungee.bungeecord.handlers;

import net.md_5.bungee.api.ProxyServer;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.net.SocketAddress;

public abstract class PacketHandler {

    private final PacketTypes[] types;

    private final Server mainServer;

    public abstract Packet handlePacket(Packet packetIn, SocketAddress address);

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
