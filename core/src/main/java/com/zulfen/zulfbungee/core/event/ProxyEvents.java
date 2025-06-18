package com.zulfen.zulfbungee.core.event;

import com.zulfen.zulfbungee.core.managers.MainServer;
import com.zulfen.zulfbungee.core.managers.transport.ChannelMainServer;
import com.zulfen.zulfbungee.core.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.core.socket.objects.client.skript.ClientPlayerDataContainer;
import com.zulfen.zulfbungee.core.socket.objects.proxy.ProxyEventPacket;
import com.zulfen.zulfbungee.core.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.core.socket.objects.proxy.ZulfProxyServer;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientInfo;

import java.util.Optional;
import java.util.UUID;

public class ProxyEvents<P, T, C> {

    protected final MainServer<P, T, C> mainServer;

    public ProxyEvents(MainServer<P, T, C> mainServerIn) {
        this.mainServer = mainServerIn;
    }

    private Optional<ClientServer> toClientServer(String nameIn) {
        Optional<ClientInfo> infoOptional = mainServer.getClientInfo(nameIn);
        return infoOptional.map(info -> new ClientServer(nameIn, info));
    }

    private Optional<ClientServer> checkValidConnection(String nameIn) {

        Optional<ZulfProxyServer<P, T, C>> serverOptional = mainServer.getImpl()
                .getServer(nameIn);


        if (serverOptional.isEmpty()) {
            return Optional.empty();
        }

        ZulfProxyServer<P, T, C> server = serverOptional.get();
        if (server.getPlayers().size() <= 1 && mainServer instanceof ChannelMainServer) {
            mainServer.removeServerConnection(server.getName(), server.getSocketAddress());
            return Optional.empty();
        }

        Optional<ClientInfo> clientInfo = mainServer.getClientInfo(server);
        return clientInfo.map(info -> new ClientServer(nameIn, info));

    }


    protected void serverConnected(ZulfProxyPlayer<P, T, C> proxyPlayerIn) {

        mainServer.sendDirectToAllAsync(new ProxyEventPacket(PacketTypes.CONNECT_EVENT, () -> mainServer.toClientPlayer(proxyPlayerIn)));

        if (proxyPlayerIn.hasPermission("zulfen.admin")) {
            mainServer.getCheckUpdateTask().checkUpdate(proxyPlayerIn, false);
        }


    }

    protected void switchServer(String toServerName, String fromServerName, String nameIn, UUID uuidIn) {

        Optional<ClientServer> transferTo = toClientServer(toServerName);
        Optional<ClientServer> transferFrom = toClientServer(fromServerName);

        if (transferFrom.isPresent() && transferTo.isPresent()) {
            mainServer.sendDirectToAllAsync(new ProxyEventPacket(PacketTypes.SERVER_SWITCH_EVENT,
                    new ClientPlayerDataContainer(transferFrom.get(), new ClientPlayer(nameIn, uuidIn, transferTo.get()))));
        }

    }

    protected void serverKick(String playerNameIn, UUID uuidIn, String reason, String previousServerName) {
        checkValidConnection(previousServerName).ifPresent(clientServer ->
                mainServer.sendDirectToAllAsync(new ProxyEventPacket(
                        PacketTypes.KICK_EVENT,
                        new ClientPlayerDataContainer(reason, new ClientPlayer(playerNameIn, uuidIn)))));

    }

    protected void serverDisconnect(String nameIn, UUID uuidIn, String previousServerName) {
        checkValidConnection(previousServerName).ifPresent(clientServer ->
                mainServer.sendDirectToAllAsync(new ProxyEventPacket(
                        PacketTypes.DISCONNECT_EVENT,
                        new ClientPlayerDataContainer(clientServer, new ClientPlayer(nameIn, uuidIn))
                ))
        );
    }


    protected synchronized void pluginMessage(String serverNameIn, byte[] dataIn) {

        Optional<ZulfProxyServer<P, T, C>> serverOptional = mainServer.getImpl().getServer(serverNameIn);
        if (serverOptional.isPresent()) {

            ZulfProxyServer<P, T, C> serverIn = serverOptional.get();
            if (mainServer instanceof ChannelMainServer<P, T, C> channelMainServer) {

                if (!channelMainServer.isChannelConnectionActive(serverNameIn)) {
                    channelMainServer.acceptMessagingConnection(serverIn.getSocketAddress(), serverNameIn,
                            dataOut -> serverIn.sendData("zproxy:channel", dataOut));

                }

                channelMainServer.proccessPluginMessage(serverNameIn, dataIn);

            }

        }

    }

}

