package com.zulfen.zulfbungee.core.handlers.proxy.packets;

import com.zulfen.zulfbungee.core.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.client.skript.SerializedNetworkVariable;
import com.zulfen.zulfbungee.core.socket.objects.client.skript.SkriptChangeMode;
import com.zulfen.zulfbungee.core.handlers.PacketHandler;
import com.zulfen.zulfbungee.core.interfaces.StorageImpl;
import com.zulfen.zulfbungee.core.managers.PacketHandlerManager;

import java.util.Optional;

public class NetworkVariableModify<P, T, C> extends PacketHandler<P, T, C> {

    public NetworkVariableModify(PacketHandlerManager<P, T, C> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> address) {

        SerializedNetworkVariable variable = (SerializedNetworkVariable) packetIn.getDataSingle();

        Optional<StorageImpl<P, T, C>> getStorage = getMainServer().getStorage();

        if (getStorage.isPresent()) {

            StorageImpl<P, T, C> storage = getStorage.get();

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
                        storage.addToVariable(variable.getName(), variable.getValueArray());
                        break;
                    case REMOVE:
                        storage.removeFromVariable(variable.getName(), variable.getValueArray());
                        break;

                }



            }

        }

        return packetIn.response(false, false);

    }
}