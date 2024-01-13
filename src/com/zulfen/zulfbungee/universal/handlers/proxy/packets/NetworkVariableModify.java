package com.zulfen.zulfbungee.universal.handlers.proxy.packets;

import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.SerializedNetworkVariable;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.SkriptChangeMode;
import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.interfaces.StorageImpl;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.Value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

public class NetworkVariableModify<P, T> extends PacketHandler<P, T> {

    public NetworkVariableModify(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> address) {

        SerializedNetworkVariable variable = (SerializedNetworkVariable) packetIn.getDataSingle();

        Optional<StorageImpl<P, T>> getStorage = getMainServer().getStorage();

        if (getStorage.isPresent()) {

            StorageImpl<P, T> storage = getStorage.get();

            if (variable.getChangeMode().isPresent()) {

                SkriptChangeMode mode = variable.getChangeMode().get();

                switch (mode) {
                    case SET:
                        storage.setVariable(variable);
                        break;

                    case DELETE:

                    case RESET:
                        storage.deleteVariable(variable.getName());
                        break;

                    case ADD:
                        Optional<SerializedNetworkVariable> optionalVariable = storage.getVariable(variable.getName());
                        Value[] existingValues = optionalVariable.map(SerializedNetworkVariable::getValueArray).orElse(new Value[0]);
                        ArrayList<Value> newValues = new ArrayList<>(Arrays.asList(existingValues));
                        Collections.addAll(newValues, variable.getValueArray());
                        storage.deleteVariable(variable.getName());
                        storage.setVariable(new SerializedNetworkVariable(variable.getName(), "ADD", newValues.toArray(new Value[newValues.size()])));
                        break;

                    case REMOVE:
                        storage.removeFromVariable(variable.getName(), variable.getValueArray());
                        break;
                }

            }

        }

        return new Packet(PacketTypes.NETWORK_VARIABLE_MODIFY, false, false, new Object[0]);

    }
}