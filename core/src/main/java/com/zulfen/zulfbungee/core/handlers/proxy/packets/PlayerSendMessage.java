package com.zulfen.zulfbungee.core.handlers.proxy.packets;

import com.zulfen.zulfbungee.core.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.core.socket.objects.client.skript.ClientPlayerDataContainer;
import com.zulfen.zulfbungee.core.handlers.PacketHandler;
import com.zulfen.zulfbungee.core.managers.PacketHandlerManager;

public class PlayerSendMessage<P, T, C> extends PacketHandler<P, T, C> {

    public PlayerSendMessage(PacketHandlerManager<P, T, C> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> address) {

        ClientPlayerDataContainer dataContainer =
                (ClientPlayerDataContainer) packetIn.getDataSingle();

        for (ClientPlayer clientPlayer : dataContainer.getPlayers()) {
            getProxy()
                    .getPlayer(clientPlayer)
                    .flatMap(getMainServer()::getConnection)
                    .ifPresent(playerConnection ->
                            playerConnection.sendDirect(
                                    packetIn.response(
                                            false,
                                            true,
                                            new ClientPlayerDataContainer(
                                                    dataContainer.getDataSingle(),
                                                    clientPlayer
                                            )
                                    )
                            )
                    );
        }



        return null;

    }
}
