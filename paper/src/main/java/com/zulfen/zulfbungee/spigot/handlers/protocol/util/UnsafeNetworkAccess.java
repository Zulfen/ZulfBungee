package com.zulfen.zulfbungee.spigot.handlers.protocol.util;

import com.comphenix.protocol.utility.MinecraftVersion;
import com.zulfen.zulfbungee.spigot.handlers.protocol.util.v1_13.V13Impl;
import com.zulfen.zulfbungee.spigot.handlers.protocol.util.v1_20_2.V202Impl;
import com.zulfen.zulfbungee.spigot.handlers.protocol.util.v1_21_4.V214Impl;
import org.bukkit.entity.Player;

public class UnsafeNetworkAccess {

    private final UnsafeNetworkHandler handler;

    public UnsafeNetworkAccess(MinecraftVersion version) {
        if (version.isAtLeast(MinecraftVersion.v1_21_0)) {
            this.handler = new V214Impl();
        } else if (version.isAtLeast(MinecraftVersion.CONFIG_PHASE_PROTOCOL_UPDATE)) {
            this.handler = new V202Impl();
        } else {
            this.handler = new V13Impl();
        }
    }

    public boolean sendPayloadPacket(Player player, byte[] payload) {
        Object preparedPayload = handler.preparePayload(player, payload);
        if (preparedPayload != null) {
            return handler.sendPacket(player, preparedPayload);
        }
        return false;
    }

}
