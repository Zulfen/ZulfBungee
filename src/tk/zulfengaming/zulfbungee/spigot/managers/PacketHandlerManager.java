package tk.zulfengaming.zulfbungee.spigot.managers;

import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.spigot.socket.packets.*;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.EnumMap;


public class PacketHandlerManager {

    private final ArrayList<PacketHandler> handlers = new ArrayList<>();

    public PacketHandlerManager(ClientConnection connectionIn) {
        addHandler(new Heartbeat(connectionIn));
        addHandler(new ConnectionName(connectionIn));
        addHandler(new ServerSwitchEvent(connectionIn));
        addHandler(new ServerMessageEvent(connectionIn));
        addHandler(new PlayerDisconnectEvent(connectionIn));
        addHandler(new PlayerConnectEvent(connectionIn));
        addHandler(new InvalidConfiguration(connectionIn));
        addHandler(new ProxyServerInfo(connectionIn));
        addHandler(new ServerKickEvent(connectionIn));
        addHandler(new GlobalScript(connectionIn));
    }

    public void addHandler(PacketHandler handlerIn) {
        handlers.add(handlerIn);
    }

    public PacketHandler getHandler(Packet packetIn) {

        for (PacketHandler handler : handlers) {
            for (PacketTypes type : handler.getTypes()) {
                if (packetIn.getType() == type) {
                    return handler;
                }
            }
        }

        return null;
    }

    // ease of use. it's an absolute pain in the arse writing it out fully every time
    public void handlePacket(Packet packetIn, SocketAddress address) {
        getHandler(packetIn).handlePacket(packetIn, address);
    }
}
