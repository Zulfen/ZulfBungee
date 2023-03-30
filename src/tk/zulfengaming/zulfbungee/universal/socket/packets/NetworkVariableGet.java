package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.interfaces.StorageImpl;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.NetworkVariable;

import java.util.Arrays;
import java.util.Optional;

public class NetworkVariableGet<P> extends PacketHandler<P> {

    public NetworkVariableGet(PacketHandlerManager<P> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection) {

        getProxy().getTaskManager().newTask(() -> {

            String variableName = (String) packetIn.getDataSingle();

            Optional<StorageImpl<P>> getStorage = getMainServer().getStorage();

            if (getStorage.isPresent()) {

                StorageImpl<P> storage = getStorage.get();

                Optional<NetworkVariable> storedVariable = storage.getVariable(variableName);

                if (storedVariable.isPresent()) {
                    NetworkVariable variable = storedVariable.get();
                    connection.sendDirect(new Packet(PacketTypes.NETWORK_VARIABLE_GET, true, false, variable));
                }

            }

        });

        return null;

    }
}