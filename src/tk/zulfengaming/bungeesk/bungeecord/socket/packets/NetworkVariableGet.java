package tk.zulfengaming.bungeesk.bungeecord.socket.packets;

import tk.zulfengaming.bungeesk.bungeecord.handlers.PacketHandler;
import tk.zulfengaming.bungeesk.bungeecord.interfaces.StorageImpl;
import tk.zulfengaming.bungeesk.bungeecord.socket.Server;
import tk.zulfengaming.bungeesk.universal.socket.Packet;
import tk.zulfengaming.bungeesk.universal.socket.PacketTypes;
import tk.zulfengaming.bungeesk.universal.utilclasses.skript.NetworkVariable;

import java.net.SocketAddress;
import java.util.Optional;
import java.util.UUID;

public class NetworkVariableGet extends PacketHandler {

    public NetworkVariableGet(Server serverIn) {
        super(serverIn, false, PacketTypes.NETWORK_VARIABLE_GET);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        String variableName = (String) packetIn.getDataSingle();
        StorageImpl storage = getMainServer().getStorage();

        getMainServer().getPluginInstance().getTaskManager().newTask(() -> {

            Optional<NetworkVariable> storedVariable = storage.getVariables(variableName);

            if (storedVariable.isPresent()) {
                NetworkVariable variable = storedVariable.get();

                getMainServer().getSocketConnection(address).send(new Packet(PacketTypes.NETWORK_VARIABLE_GET, false, false, variable));

            } else {
                getMainServer().getPluginInstance().warning("Couldn't find variable " + variableName + "! Please check the name!");
            }

        }, "StorageOperation@" + UUID.randomUUID());

        return null;
    }
}