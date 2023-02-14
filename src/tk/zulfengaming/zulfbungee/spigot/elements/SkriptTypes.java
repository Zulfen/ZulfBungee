package tk.zulfengaming.zulfbungee.spigot.elements;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.NetworkVariable;

public class SkriptTypes {

    static {
        Classes.registerClass(new ClassInfo<>(ClientPlayer.class, "proxyplayer")
                .user("proxyplayers?")
                .name("Proxy Player")
                .description("Represents a player on the Bungeecord network.")
                .defaultExpression(new EventValueExpression<>(ClientPlayer.class))
                .parser(new Parser<ClientPlayer>() {

                    @Override
                    public ClientPlayer parse(@NotNull String s, @NotNull ParseContext context) {
                        return null;
                    }

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(ClientPlayer proxyPlayer, int i) {
                        return proxyPlayer.getName();
                    }

                    @Override
                    public @NotNull String toVariableNameString(ClientPlayer proxyPlayer) {
                        return proxyPlayer.getName();
                    }

                }));

        Classes.registerClass(new ClassInfo<>(ClientServer.class, "proxyserver")
                .user("proxyservers?")
                .name("Proxy Server")
                .description("Represents a network variable.")
                .defaultExpression(new EventValueExpression<>(ClientServer.class))
                .parser(new Parser<ClientServer>() {

                    @Override
                    public ClientServer parse(@NotNull String s, @NotNull ParseContext context) {
                        return null;
                    }

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(ClientServer zulfProxyServer, int i) {
                        return zulfProxyServer.getName();
                    }

                    @Override
                    public @NotNull String toVariableNameString(ClientServer zulfProxyServer) {
                        return zulfProxyServer.getName();
                    }

                }));

        Classes.registerClass(new ClassInfo<>(NetworkVariable.class, "networkvariable")
                .user("networkvariables?")
                .name("Proxy Server")
                .description("Represents a proxied server.")
                .defaultExpression(new EventValueExpression<>(NetworkVariable.class))
                .parser(new Parser<NetworkVariable>() {

                    @Override
                    public NetworkVariable parse(@NotNull String s, @NotNull ParseContext context) {
                        return null;
                    }

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return false;
                    }

                    @Override
                    public @NotNull String toString(NetworkVariable networkVariable, int i) {
                        return networkVariable.getName();
                    }

                    @Override
                    public @NotNull String toVariableNameString(NetworkVariable networkVariable) {
                        return networkVariable.getName();
                    }

                }));

    }
}
