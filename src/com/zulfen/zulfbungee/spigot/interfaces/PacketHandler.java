package com.zulfen.zulfbungee.spigot.interfaces;

import com.zulfen.zulfbungee.spigot.socket.Connection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;

public abstract class PacketHandler {

    private final PacketTypes[] types;

    private final Connection<?> connection;

    private final boolean isAsync;

    public abstract void handlePacket(Packet packetIn);

    public PacketTypes[] getTypes() {
        return types;
    }

    public Connection<?> getConnection() {
        return connection;
    }

    public PacketHandler(Connection<?> connectionIn, boolean isAsync, PacketTypes... types){
        this.connection = connectionIn;
        this.types = types;
        this.isAsync = isAsync;
    }

    public boolean isAsync() {
        return isAsync;
    }

}
