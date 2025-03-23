package com.zulfen.zulfbungee.velocity.command;

import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.zulfen.zulfbungee.core.command.ProxyCommandSender;
import com.zulfen.zulfbungee.velocity.interfaces.ZulfVelocityImpl;

public class VelocityConsole implements ProxyCommandSender {

    private final ZulfVelocityImpl velocity;
    private final ConsoleCommandSource consoleCommandSource;

    public VelocityConsole(ZulfVelocityImpl velocityIn) {
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
