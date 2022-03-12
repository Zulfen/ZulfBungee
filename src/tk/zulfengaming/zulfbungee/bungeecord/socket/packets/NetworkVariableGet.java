package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.StorageImpl;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.NetworkVariable;

import java.util.Optional;

public class NetworkVariableGet extends PacketHandler {

    public NetworkVariableGet(Server serverIn) {
        super(serverIn, PacketTypes.NETWORK_VARIABLE_GET);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection address) {

        String variableName = (String) packetIn.getDataSingle();

        Optional<StorageImpl> getStorage = getMainServer().getStorage();

        if (getStorage.isPresent()) {

            StorageImpl storage = getStorage.get();

            Optional<NetworkVariable> storedVariable = storage.getVariables(variableName);
            NetworkVariable variable = null;

            if (storedVariable.isPresent()) {
                variable = storedVariable.get();

            }

            return new Packet(PacketTypes.NETWORK_VARIABLE_GET, false, false, variable);

        }

        return null;
    }
}