package tk.zulfengaming.zulfbungee.universal.interfaces;

import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.util.Optional;
import java.util.function.Function;

public class NativePlayerConverter<T, P> implements Function<T, Optional<ZulfProxyPlayer<P, T>>> {
    @Override
    public Optional<ZulfProxyPlayer<P, T>> apply(T nativePlayer) {
        return Optional.empty();
    }
}
