package tk.zulfengaming.bungeesk.bungeecord.handlers;

import net.md_5.bungee.api.ProxyServer;
import tk.zulfengaming.bungeesk.bungeecord.socket.Server;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;

import java.net.SocketAddress;

public abstract class PacketHandler {

    private final PacketTypes[] types;

    private final boolean debugVisibility;

    private final Server socketServer;

    public abstract Packet handlePacket(Packet packetIn, SocketAddress address);

    public PacketTypes[] getTypes() {
        return types;
    }

    public Server getSocketServer() {
        return socketServer;
    }

    public ProxyServer getProxy() {
        return socketServer.getPluginInstance().getProxy();
    }

    public boolean shouldHideInDebug() {
        return debugVisibility;
    }

    public PacketHandler(Server serverIn, boolean hideIn, PacketTypes... types) {
        this.socketServer = serverIn;
        this.types = types;

        this.debugVisibility = hideIn;

    }

}
