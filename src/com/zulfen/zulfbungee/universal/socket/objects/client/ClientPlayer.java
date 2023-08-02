package com.zulfen.zulfbungee.universal.socket.objects.client;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.UUID;

public class ClientPlayer implements Serializable {

    private final String name;

    private final UUID uuid;

    private ClientServer server;
    private InetSocketAddress address;
    private InetSocketAddress virtualHost;

    public ClientPlayer(String name, UUID uuid) {
        this.name = name;
        this.uuid = uuid;
    }

    public ClientPlayer(String name, UUID uuid, ClientServer serverIn) {
        this.name = name;
        this.uuid = uuid;
        this.server = serverIn;
    }

    public ClientPlayer(String name, UUID uuid, ClientServer serverIn, InetSocketAddress addressIn, InetSocketAddress virHostIn) {
        this.name = name;
        this.uuid = uuid;
        this.server = serverIn;
        this.address = addressIn;
        this.virtualHost = virHostIn;
    }

    public ClientPlayer(String name, UUID uuid, ClientServer serverIn, InetSocketAddress addressIn) {
        this.name = name;
        this.uuid = uuid;
        this.server = serverIn;
        this.address = addressIn;
    }

    public ClientPlayer(String name, UUID uuid, InetSocketAddress addressIn) {
        this.name = name;
        this.uuid = uuid;
        this.address = addressIn;
    }

    public Optional<ClientServer> getServer() {
        return Optional.ofNullable(server);
    }

    public Optional<InetSocketAddress> getVirtualHost() {
        return Optional.ofNullable(virtualHost);
    }

    public Optional<InetSocketAddress> getAddress() {
        return Optional.ofNullable(address);
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

}
