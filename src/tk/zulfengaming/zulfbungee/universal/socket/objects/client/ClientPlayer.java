package tk.zulfengaming.zulfbungee.universal.socket.objects.client;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

public class ClientPlayer implements Serializable {

    private final String name;

    private final UUID uuid;

    private ClientServer server;

    public ClientPlayer(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public ClientPlayer(String name, UUID uuid, ClientServer serverIn) {
        this.name = name;
        this.uuid = uuid;
        this.server = serverIn;
    }

    public Optional<ClientServer> getServer() {
        return Optional.ofNullable(server);
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

}
