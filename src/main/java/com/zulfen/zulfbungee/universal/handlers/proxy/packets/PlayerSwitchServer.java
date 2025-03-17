package com.zulfen.zulfbungee.universal.handlers.proxy.packets;

import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ClientPlayerDataContainer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;

import java.util.Optional;

public class PlayerSwitchServer<P, T, C> extends PacketHandler<P, T, C> {

    public PlayerSwitchServer(PacketHandlerManager<P, T, C> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> address) {

        ClientPlayerDataContainer switchEvent = (ClientPlayerDataContainer) packetIn.getDataSingle();

        ClientServer clientServer = (ClientServer) switchEvent.getDataSingle();

        if (clientServer != null) {

            Optional<ZulfProxyServer<P, T, C>> server = getProxy().getServer(clientServer);

            if (server.isPresent()) {

                for (ClientPlayer clientPlayer : switchEvent.getPlayers()) {

                    Optional<ZulfProxyPlayer<P, T, C>> proxyPlayer = getProxy().getPlayer(clientPlayer);
                    proxyPlayer.ifPresent(pZulfProxyPlayer -> pZulfProxyPlayer.connect(server.get()));

                }

            }

        }

        return null;
    }
}
