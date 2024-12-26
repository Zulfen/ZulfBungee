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
                return new Packet(PacketTypes.NETWORK_VARIABLE_GET, true, false, variable);
            }

        }

        return new Packet(PacketTypes.NETWORK_VARIABLE_GET, true, false, new Value[0]);

    }
}