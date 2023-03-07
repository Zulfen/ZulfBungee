package tk.zulfengaming.zulfbungee.universal.socket.packets;

import tk.zulfengaming.zulfbungee.universal.handlers.PacketHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ConsoleExecutableCommand;

import java.util.Optional;

public class ConsoleCommand<P> extends PacketHandler<P> {

    public ConsoleCommand(PacketHandlerManager<P> packetHandlerManager) {
        super(packetHandlerManager);
    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection<P> connection) {

        ConsoleExecutableCommand consoleExecutableCommand = (ConsoleExecutableCommand) packetIn.getDataSingle();

        for (ClientServer clientServer : consoleExecutableCommand.getServers()) {

            Optional<BaseServerConnection<P>> getOtherConn = getMainServer().getConnection(clientServer);
            getOtherConn.ifPresent(pBaseServerConnection -> pBaseServerConnection.sendDirect(new Packet(PacketTypes.CONSOLE_EXECUTE_COMMAND, false, true, consoleExecutableCommand.getCommand())));

        }

        return null;

    }
}
