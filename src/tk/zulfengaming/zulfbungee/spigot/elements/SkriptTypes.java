package tk.zulfengaming.zulfbungee.spigot.elements;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyServer;

public class SkriptTypes {

    static {
        Classes.registerClass(new ClassInfo<>(ProxyPlayer.class, "proxyplayer")
                .user("proxyplayers?")
                .name("Proxy Player")
                .description("Represents a player on the Bungeecord network.")
                .defaultExpression(new EventValueExpression<>(ProxyPlayer.class))
                .parser(new Parser<ProxyPlayer>() {

                    @Override
                    public ProxyPlayer parse(@NotNull String s, @NotNull ParseContext context) {
                        return null;
                    }

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(ProxyPlayer proxyPlayer, int i) {
                        return proxyPlayer.getName();
                    }

                    @Override
                    public @NotNull String toVariableNameString(ProxyPlayer proxyPlayer) {
                        return proxyPlayer.getName();
                    }

                }));

        Classes.registerClass(new ClassInfo<>(ProxyServer.class, "proxyserver")
                .user("proxyservers?")
                .name("Proxy Server")
                .description("Represents a proxied server.")
                .defaultExpression(new EventValueExpression<>(ProxyServer.class))
                .parser(new Parser<ProxyServer>() {

                    @Override
                    public ProxyServer parse(@NotNull String s, @NotNull ParseContext context) {
                        return null;
                    }

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(ProxyServer proxyServer, int i) {
                        return proxyServer.getName();
                    }

                    @Override
                    public @NotNull String toVariableNameString(ProxyServer proxyServer) {
                        return proxyServer.getName();
                    }

                }));
    }
}
