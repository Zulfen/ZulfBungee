package com.zulfen.zulfbungee.core.handlers.proxy.packets;

import com.zulfen.zulfbungee.core.handlers.PacketHandler;
import com.zulfen.zulfbungee.core.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.core.socket.ProxyServerConnection;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.core.socket.objects.client.skript.ConsoleExecutableCommand;

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
