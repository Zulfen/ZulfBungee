package tk.zulfengaming.zulfbungee.spigot.handlers;

import tk.zulfengaming.zulfbungee.spigot.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.spigot.socket.ClientConnection;
import tk.zulfengaming.zulfbungee.spigot.socket.packets.*;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.net.SocketAddress;
import java.util.EnumMap;


public class PacketHandlerManager {

    private final EnumMap<PacketTypes, PacketHandler> handlers = new EnumMap<>(PacketTypes.class);

    public PacketHandlerManager(ClientConnection connectionIn) {
        addHandler(new Heartbeat(connectionIn));
        addHandler(new ClientUpdate(connectionIn));
        addHandler(new ServerSwitchEvent(connectionIn));
        addHandler(new ServerMessageEvent(connectionIn));
        addHandler(new PlayerDisconnectEvent(connectionIn));
        addHandler(new PlayerConnectEvent(connectionIn));
        addHandler(new InvalidConfiguration(connectionIn));
        addHandler(new ClientInfo(connectionIn));
        addHandler(new ServerKickEvent(connectionIn));
        addHandler(new GlobalScriptData(connectionIn));
    }

    public void addHandler(PacketHandler handlerIn) {
        for (PacketTypes type : handlerIn.getTypes()) {
            handlers.put(type, handlerIn);
        }
    }

    public PacketHandler getHandler(Packet packetIn) {
        return handlers.get(packetIn.getType());
    }

    // ease of use. it's an absolute pain in the arse writing it out fully every time
    public void handlePacket(Packet packetIn, SocketAddress address) {
        getHandler(packetIn).handlePacket(packetIn, address);
    }
}
