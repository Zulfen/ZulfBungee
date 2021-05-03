package tk.zulfengaming.bungeesk.bungeecord.interfaces;

import tk.zulfengaming.bungeesk.bungeecord.socket.Server;
import tk.zulfengaming.bungeesk.universal.utilclasses.skript.NetworkVariable;

import java.util.Optional;

// TODO: Since this only needs to handle doing things with Values, just make the return and function arguments those
// and implement it for each storage type.
public abstract class StorageImpl {

    private final Server mainServer;

    public StorageImpl(Server serverIn) {
        mainServer = serverIn;
    }

    public abstract void initialise();

    public abstract Optional<NetworkVariable> getVariables(String name);

    public abstract void setVariables(NetworkVariable variable);

    public abstract void deleteVariables(String name);

    public abstract void shutdown();

    public Server getMainServer() {
        return mainServer;
    }
}
