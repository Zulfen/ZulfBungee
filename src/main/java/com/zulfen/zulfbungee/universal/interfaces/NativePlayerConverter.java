package com.zulfen.zulfbungee.universal.interfaces;

import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;
import java.util.function.Function;

public class NativePlayerConverter<P, T, C> implements Function<T, Optional<ZulfProxyPlayer<P, T, C>>> {
    @Override
    public Optional<ZulfProxyPlayer<P, T, C>> apply(T nativePlayer) {
        return Optional.empty();
    }
}
