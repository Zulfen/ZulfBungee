package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.StorageImpl;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.NetworkVariable;
import tk.zulfengaming.zulfbungee.universal.util.skript.SkriptChangeMode;

import java.net.SocketAddress;
import java.util.Optional;
import java.util.UUID;

public class NetworkVariableModify extends PacketHandler {

    public NetworkVariableModify(Server serverIn) {
        super(serverIn, PacketTypes.NETWORK_VARIABLE_MODIFY);

    }

    @Override
    public Packet handlePacket(Packet packetIn, SocketAddress address) {

        NetworkVariable variable = (NetworkVariable) packetIn.getDataSingle();

        Optional<StorageImpl> getStorage = getMainServer().getStorage();

        if (getStorage.isPresent()) {

            StorageImpl storage = getStorage.get();

            if (variable.getChangeMode().isPresent()) {
                getMainServer().getPluginInstance().getTaskManager().newTask(() -> {

                    SkriptChangeMode mode = variable.getChangeMode().get();

                    switch (mode) {

                        case SET:
                            storage.setVariables(variable);
                            break;
                        case DELETE:
                            storage.deleteVariables(variable.getName());
                            break;
                        case ADD:
                            storage.addToVariable(variable.getName(), variable.getValueArray());
                            break;
                        case REMOVE:
                            storage.removeFromVariable(variable.getName(), variable.getValueArray());
                            break;

                    }

                    getMainServer().getSocketConnections().get(address).send(new Packet(PacketTypes.NETWORK_VARIABLE_MODIFY, false, false, null));

                }, "StorageOperation@" + UUID.randomUUID());
            }

        }

        return null;
    }
}