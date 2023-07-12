package tk.zulfengaming.zulfbungee.universal.handlers.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.interfaces.ProxyServerConnection;
import tk.zulfengaming.zulfbungee.universal.interfaces.StorageImpl;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.NetworkVariable;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.Value;

import java.util.Optional;

public class NetworkVariableGet<P, T> extends PacketHandler<P, T> {

    public NetworkVariableGet(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connection) {

        getProxy().getTaskManager().newTask(() -> {

            String variableName = (String) packetIn.getDataSingle();

            Optional<StorageImpl<P, T>> getStorage = getMainServer().getStorage();

            if (getStorage.isPresent()) {

                StorageImpl<P, T> storage = getStorage.get();

                Optional<NetworkVariable> storedVariable = storage.getVariable(variableName);

                if (storedVariable.isPresent()) {
                    NetworkVariable variable = storedVariable.get();
                    connection.sendDirect(new Packet(PacketTypes.NETWORK_VARIABLE_GET, true, false, variable));
                    return;
                }

            }

            connection.sendDirect(new Packet(PacketTypes.NETWORK_VARIABLE_GET, true, false, new Value[0]));

        });

        return null;

    }
}