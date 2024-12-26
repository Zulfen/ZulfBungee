package com.zulfen.zulfbungee.universal.handlers.proxy.packets;

import com.zulfen.zulfbungee.universal.handlers.PacketHandler;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ConsoleExecutableCommand;

import java.util.Optional;

public class ConsoleCommand<P, T, C> extends PacketHandler<P, T, C> {

    public ConsoleCommand(PacketHandlerManager<P, T, C> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T, C> connection) {

        ConsoleExecutableCommand consoleExecutableCommand = (ConsoleExecutableCommand) packetIn.getDataSingle();

        for (ClientServer clientServer : consoleExecutableCommand.getServers()) {

            Optional<ProxyServerConnection<P, T, C>> getOtherConn = getMainServer().getConnection(clientServer);
            getOtherConn.ifPresent(pBaseServerConnection -> pBaseServerConnection.sendDirect(new Packet(PacketTypes.CONSOLE_EXECUTE_COMMAND, false, true, consoleExecutableCommand.getCommand())));

        }

        return null;

    }
}
