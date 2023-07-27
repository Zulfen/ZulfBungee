package com.zulfen.zulfbungee.universal.interfaces;

import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;
import java.util.function.Function;

public class NativePlayerConverter<P, T> implements Function<T, Optional<ZulfProxyPlayer<P, T>>> {
    @Override
    public Optional<ZulfProxyPlayer<P, T>> apply(T nativePlayer) {
        return Optional.empty();
    }
}
