package tk.zulfengaming.zulfbungee.bungeecord.interfaces;

import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.util.skript.NetworkVariable;
import tk.zulfengaming.zulfbungee.universal.util.skript.Value;

import java.util.Optional;

public abstract class StorageImpl {

    private final Server mainServer;

    private final String host, port, username, password, database;

    public StorageImpl(Server serverIn) {
        this.mainServer = serverIn;

        this.host = serverIn.getPluginInstance().getConfig().getString("storage-host");
        this.port = String.valueOf(serverIn.getPluginInstance().getConfig().getInt("storage-port"));

        this.username = serverIn.getPluginInstance().getConfig().getString("storage-username");
        this.password = serverIn.getPluginInstance().getConfig().getString("storage-password");

        this.database = serverIn.getPluginInstance().getConfig().getString("storage-database");
    }
    
    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getDatabase() {
        return database;
    }

    public abstract void setupDatabase();

    public abstract Optional<NetworkVariable> getVariables(String name);

    public abstract void setVariables(NetworkVariable variable);

    public abstract void addToVariable(String name, Value[] values);

    public abstract void deleteVariables(String name);

    public abstract void removeFromVariable(String name, Value[] values);

    public abstract void shutdown();

    public Server getMainServer() {
        return mainServer;
    }
}
