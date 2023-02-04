package tk.zulfengaming.zulfbungee.universal.managers;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.packets.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Optional;

public class PacketHandlerManager<P> {

    private final EnumMap<PacketTypes, PacketHandler<P>> handlers = new EnumMap<>(PacketTypes.class);
    private final MainServer<P> mainServer;

    public PacketHandlerManager(MainServer<P> mainServerIn) {

        this.mainServer = mainServerIn;

        handlers.put(PacketTypes.GLOBAL_SCRIPT, new GlobalScript<>(this));
        handlers.put(PacketTypes.HEARTBEAT, new Heartbeat<>(this));
        handlers.put(PacketTypes.NETWORK_VARIABLE_GET, new NetworkVariableGet<>(this));
        handlers.put(PacketTypes.NETWORK_VARIABLE_MODIFY, new NetworkVariableModify<>(this));
        handlers.put(PacketTypes.PLAYER_SEND_MESSAGE, new PlayerSendMessage<>(this));
        handlers.put(PacketTypes.PLAYER_SERVER, new PlayerServer<>(this));
        handlers.put(PacketTypes.PLAYER_SWITCH_SERVER, new PlayerSwitchServer<>(this));
        handlers.put(PacketTypes.PROXY_CLIENT_INFO, new ProxyClientInfo<>(this));
        handlers.put(PacketTypes.PLAYER_ONLINE, new ProxyPlayerOnline<>(this));
        handlers.put(PacketTypes.PROXY_PLAYERS, new ProxyPlayers<>(this));
        handlers.put(PacketTypes.PROXY_PLAYER_UUID, new ProxyPlayerUUID<>(this));
        handlers.put(PacketTypes.SERVER_SEND_MESSAGE_EVENT, new ServerSendMessage<>(this));

    }

    public Optional<PacketHandler<P>> getHandler(PacketTypes type) {
        return Optional.ofNullable(handlers.get(type));
    }

    // ease of use. it's an absolute pain in the arse writing it out fully every time
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection) {
        PacketTypes type = packetIn.getType();
        if (getHandler(type).isPresent()) {
            return getHandler(type).get().handlePacket(packetIn, connection);
        } else {
            throw new RuntimeException(String.format("Could not find handler for packet type %s", type));
        }
    }

    public MainServer<P> getMainServer() {
        return mainServer;
    }

}
