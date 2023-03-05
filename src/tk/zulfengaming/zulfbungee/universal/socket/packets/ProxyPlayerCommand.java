package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.PlayerCommand;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;

public class ProxyPlayerCommand<P> extends PacketHandler<P> {

    public ProxyPlayerCommand(PacketHandlerManager<P> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection) {

        PlayerCommand playerCommand = (PlayerCommand) packetIn.getDataSingle();

        for (ClientPlayer clientPlayer : playerCommand.getPlayers()) {

            Optional<ZulfProxyPlayer<P>> player = getProxy().getPlayer(clientPlayer);

            if (player.isPresent()) {
                Optional<BaseServerConnection<P>> serverConnection = getMainServer().getConnection(player.get());
                serverConnection.ifPresent(pBaseServerConnection -> pBaseServerConnection.sendDirect(packetIn));
            }
        }

        return null;

    }
}
