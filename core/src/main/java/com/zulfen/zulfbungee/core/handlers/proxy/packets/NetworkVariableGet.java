package com.zulfen.zulfbungee.core.handlers.proxy.packets;

import com.zulfen.zulfbungee.core.handlers.PacketHandler;
import com.zulfen.zulfbungee.core.interfaces.StorageImpl;
import com.zulfen.zulfbungee.core.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.core.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.client.skript.SerializedNetworkVariable;
import com.zulfen.zulfbungee.core.socket.objects.client.skript.Value;

import java.util.Optional;

public class NetworkVariableGet<P, T, C> extends PacketHandler<P, T, C> {

    public NetworkVariableGet(PacketHandlerManager<P, T, C> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> connection) {

        String variableName = (String) packetIn.getDataSingle();

        Optional<StorageImpl<P, T, C>> getStorage = getMainServer().getStorage();

        if (getStorage.isPresent()) {

            StorageImpl<P, T, C> storage = getStorage.get();

            Optional<SerializedNetworkVariable> storedVariable = storage.getVariable(variableName);

            if (storedVariable.isPresent()) {
                SerializedNetworkVariable variable = storedVariable.get();
                return packetIn.response(true, false, variable);
            }

        }

        return packetIn.response(true, false, new Value[0]);

    }
}