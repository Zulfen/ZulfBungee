package com.zulfen.zulfbungee.universal.handlers.proxy.packets;

import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.interfaces.StorageImpl;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.SerializedNetworkVariable;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.Value;

import java.util.Optional;

public class NetworkVariableGet<P, T> extends PacketHandler<P, T> {

    public NetworkVariableGet(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connection) {

        String variableName = (String) packetIn.getDataSingle();

        Optional<StorageImpl<P, T>> getStorage = getMainServer().getStorage();

        if (getStorage.isPresent()) {

            StorageImpl<P, T> storage = getStorage.get();

            Optional<SerializedNetworkVariable> storedVariable = storage.getVariable(variableName);

            if (storedVariable.isPresent()) {
                SerializedNetworkVariable variable = storedVariable.get();
                return new Packet(PacketTypes.NETWORK_VARIABLE_GET, true, false, variable);
            }

        }

        return new Packet(PacketTypes.NETWORK_VARIABLE_GET, true, false, new Value[0]);

    }
}