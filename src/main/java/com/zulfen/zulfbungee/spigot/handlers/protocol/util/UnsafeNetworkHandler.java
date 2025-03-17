package com.zulfen.zulfbungee.spigot.handlers.protocol.util;

import org.bukkit.entity.Player;

public interface UnsafeNetworkHandler {
    Object preparePayload(Player player, byte[] payload);
    boolean sendPacket(Player player, Object packet);
}
