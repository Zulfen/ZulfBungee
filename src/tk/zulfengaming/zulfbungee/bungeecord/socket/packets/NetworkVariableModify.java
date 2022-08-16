package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.StorageImpl;
import tk.zulfengaming.zulfbungee.bungeecord.socket.MainServer;
import tk.zulfengaming.zulfbungee.bungeecord.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.NetworkVariable;
import tk.zulfengaming.zulfbungee.universal.util.skript.SkriptChangeMode;

import java.util.Optional;

public class NetworkVariableModify extends PacketHandler {

    public NetworkVariableModify(MainServer mainServerIn) {
        super(mainServerIn, PacketTypes.NETWORK_VARIABLE_MODIFY);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection address) {

        NetworkVariable variable = (NetworkVariable) packetIn.getDataSingle();

        Optional<StorageImpl> getStorage = getMainServer().getStorage();

        if (getStorage.isPresent()) {

            StorageImpl storage = getStorage.get();

            if (variable.getChangeMode().isPresent()) {

                SkriptChangeMode mode = variable.getChangeMode().get();

                switch (mode) {

                    case SET:
                        storage.setVariable(variable);
                        break;
                    case DELETE:
                        storage.deleteVariable(variable.getName());
                        break;
                    case ADD:
                        storage.addToVariable(variable.getName(), variable.getValueArray());
                        break;
                    case REMOVE:
                        storage.removeFromVariable(variable.getName(), variable.getValueArray());
                        break;

                }

                return new Packet(PacketTypes.NETWORK_VARIABLE_MODIFY, false, false, new Object[0]);

            }

        }

        return null;
    }
}