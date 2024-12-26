package com.zulfen.zulfbungee.spigot.interfaces;

import com.zulfen.zulfbungee.spigot.socket.ClientConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;

public abstract class PacketHandler {

    private final PacketTypes[] types;

    private final ClientConnection<?> connection;

    private final boolean isAsync;

    public abstract void handlePacket(Packet packetIn);

    public PacketTypes[] getTypes() {
        return types;
    }

    public ClientConnection<?> getConnection() {
        return connection;
    }

    public PacketHandler(ClientConnection<?> connectionIn, boolean isAsync, PacketTypes... types){
        this.connection = connectionIn;
        this.types = types;
        this.isAsync = isAsync;
    }

    public boolean isAsync() {
        return isAsync;
    }

}
