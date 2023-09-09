package com.zulfen.zulfbungee.universal.interfaces;

import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.SerializedNetworkVariable;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.Value;

import java.util.Optional;

public abstract class StorageImpl<P, T> {

    private final MainServer<P, T> mainServer;

    private final String host, port, username, password, database;

    public StorageImpl(MainServer<P, T> mainServerIn) {
        this.mainServer = mainServerIn;

        this.host = mainServerIn.getImpl().getConfig().getString("storage-host");
        this.port = String.valueOf(mainServerIn.getImpl().getConfig().getInt("storage-port"));

        this.username = mainServerIn.getImpl().getConfig().getString("storage-username");
        this.password = mainServerIn.getImpl().getConfig().getString("storage-password");

        this.database = mainServerIn.getImpl().getConfig().getString("storage-database");
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

    public abstract Optional<SerializedNetworkVariable> getVariable(String name);

    public abstract void setVariable(SerializedNetworkVariable variable);

    public abstract void addToVariable(String name, Value[] values);

    public abstract void deleteVariable(String name);

    public abstract void removeFromVariable(String name, Value[] values);

    public abstract void shutdown();

    public MainServer<P, T> getMainServer() {
        return mainServer;
    }
}
