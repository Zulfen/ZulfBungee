package tk.zulfengaming.zulfbungee.universal.handlers.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.interfaces.ProxyServerConnection;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ConsoleExecutableCommand;

import java.util.Optional;

public class ConsoleCommand<P, T> extends PacketHandler<P, T> {

    public ConsoleCommand(PacketHandlerManager<P, T> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, ProxyServerConnection<P, T> connection) {

        ConsoleExecutableCommand consoleExecutableCommand = (ConsoleExecutableCommand) packetIn.getDataSingle();

        for (ClientServer clientServer : consoleExecutableCommand.getServers()) {

            Optional<ProxyServerConnection<P, T>> getOtherConn = getMainServer().getConnection(clientServer);
            getOtherConn.ifPresent(pBaseServerConnection -> pBaseServerConnection.sendDirect(new Packet(PacketTypes.CONSOLE_EXECUTE_COMMAND, false, true, consoleExecutableCommand.getCommand())));

        }

        return null;

    }
}
