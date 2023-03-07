package tk.zulfengaming.zulfbungee.velocity.command;

import com.velocitypowered.api.proxy.ProxyServer;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.velocity.ZulfVelocity;

public class VelocityConsole implements ProxyCommandSender<ProxyServer> {

    private final ZulfVelocity velocity;

    public VelocityConsole(ZulfVelocity velocityIn) {
        this.velocity = velocityIn;
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
        velocity.getPlatform().getConsoleCommandSource()
                .sendMessage(velocity.getLegacyTextSerialiser().deserialize(message));
    }
}
