package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.StorageImpl;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.NetworkVariable;

import java.net.SocketAddress;
import java.util.Optional;
import java.util.UUID;

public class NetworkVariableGet extends PacketHandler {

    public NetworkVariableGet(Server serverIn) {
        super(serverIn, PacketTypes.NETWORK_VARIABLE_GET);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        String variableName = (String) packetIn.getDataSingle();

        Optional<StorageImpl> getStorage = getMainServer().getStorage();

        if (getStorage.isPresent()) {

            StorageImpl storage = getStorage.get();

            getMainServer().getPluginInstance().getTaskManager().newTask(() -> {

                Optional<NetworkVariable> storedVariable = storage.getVariables(variableName);

                if (storedVariable.isPresent()) {
                    NetworkVariable variable = storedVariable.get();

                    // sent async instead of from main ServerConnection thread to prevent it locking up.
                    getMainServer().getSocketConnections().get(address).send(new Packet(PacketTypes.NETWORK_VARIABLE_GET, false, false, variable));

                } else {
                    getMainServer().getPluginInstance().warning("Couldn't find variable " + variableName + "! Please check the name!");
                }

            }, "StorageOperation@" + UUID.randomUUID());

        }

        return null;
    }
}