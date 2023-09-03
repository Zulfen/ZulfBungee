package com.zulfen.zulfbungee.velocity.command;

import com.velocitypowered.api.proxy.ConsoleCommandSource;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ProxyServer;
import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import com.zulfen.zulfbungee.velocity.interfaces.ZulfVelocityImpl;

public class VelocityConsole implements ProxyCommandSender<ProxyServer, Player> {

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
