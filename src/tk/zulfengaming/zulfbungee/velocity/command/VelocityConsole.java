package tk.zulfengaming.zulfbungee.velocity.command;

import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.velocity.ZulfVelocity;

public class VelocityConsole implements ProxyCommandSender<ProxyServer, Player> {

    private final ZulfVelocity velocity;
    private final ConsoleCommandSource consoleCommandSource;

    public VelocityConsole(ZulfVelocity velocityIn) {
        this.velocity = velocityIn;
        this.consoleCommandSource = velocity.getPlatform().getConsoleCommandSource();
    }

    @Override
    public boolean isPlayer() {
        return false;
    }

    @Override
    public boolean hasPermission(String permission) {
        return true;
    }

    @Override
    public void sendMessage(String message) {
       consoleCommandSource.sendMessage(velocity.getLegacyTextSerializer().deserialize(message));
    }
}
