package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.interfaces.StorageImpl;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.NetworkVariable;

import java.util.Optional;

public class NetworkVariableGet<P> extends PacketHandler<P> {

    public NetworkVariableGet(MainServer<P> mainServerIn) {
        super(mainServerIn, PacketTypes.NETWORK_VARIABLE_GET);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> address) {

        String variableName = (String) packetIn.getDataSingle();

        Optional<StorageImpl<P>> getStorage = getMainServer().getStorage();

        if (getStorage.isPresent()) {

            StorageImpl<P> storage = getStorage.get();

            Optional<NetworkVariable> storedVariable = storage.getVariable(variableName);
            NetworkVariable variable = null;

            if (storedVariable.isPresent()) {
                variable = storedVariable.get();

            }

            return new Packet(PacketTypes.NETWORK_VARIABLE_GET, false, false, variable);

        }

        return null;
    }
}