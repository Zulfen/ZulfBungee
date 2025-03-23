package com.zulfen.zulfbungee.core.managers;

import com.zulfen.zulfbungee.core.handlers.proxy.packets.*;
import com.zulfen.zulfbungee.core.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.core.handlers.PacketHandler;

import java.util.EnumMap;

public class PacketHandlerManager<P, T, C> {

    private final EnumMap<PacketTypes, PacketHandler<P, T, C>> handlers = new EnumMap<>(PacketTypes.class);
    private final MainServer<P, T, C> mainServer;

    public PacketHandlerManager(MainServer<P, T, C> mainServerIn) {

        this.mainServer = mainServerIn;

        handlers.put(PacketTypes.GLOBAL_SCRIPT, new GlobalScript<>(this));
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
        handlers.put(PacketTypes.KICK_PLAYER, new PlayerKick<>(this));
        handlers.put(PacketTypes.PLAYER_EXECUTE_COMMAND, new ProxyPlayerCommand<>(this));
        handlers.put(PacketTypes.CONSOLE_EXECUTE_COMMAND, new ConsoleCommand<>(this));
        handlers.put(PacketTypes.BROADCAST_MESSAGE, new ProxyBroadcast<>(this));
        handlers.put(PacketTypes.PROXY_PLAYER_PERMISSION, new ProxyPlayerPermission<>(this));
        handlers.put(PacketTypes.CONNECTION_NAME, new ConnectionName<>(this));
        handlers.put(PacketTypes.PROXY_PLAYER_IP, new ProxyPlayerIP<>(this));
        handlers.put(PacketTypes.PLAYER_VIRTUAL_HOST, new ProxyPlayerVirtualHost<>(this));
        handlers.put(PacketTypes.REGISTER_SERVER, new RegisterServer<>(this));
        handlers.put(PacketTypes.DEREGISTER_SERVER, new DeRegisterServer<>(this));
        handlers.put(PacketTypes.KEEP_ALIVE, new KeepAlive<>(this));

    }

    // ease of use. it's an absolute pain in the arse writing it out fully every time
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> connection) {
        PacketTypes type = packetIn.getType();
        PacketHandler<P, T, C> handler = handlers.get(type);
        if (handler != null) {
            return handler.handlePacket(packetIn, connection);
        } else {
            throw new RuntimeException(String.format("Could not find handler for packet type %s", type));
        }
    }

    public MainServer<P, T, C> getMainServer() {
        return mainServer;
    }

}
