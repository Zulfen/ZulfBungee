package tk.zulfengaming.zulfbungee.spigot.elements;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.spigot.ZulfBungeeSpigot;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.Optional;
import java.util.UUID;

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

                        Optional<Packet> playerRequest = ZulfBungeeSpigot.getPlugin().getConnection()
                                .send(new Packet(PacketTypes.PROXY_PLAYER_UUID, true, false, s));

                        if (playerRequest.isPresent()) {
                            Packet packet = playerRequest.get();
                            if (packet.getDataArray().length != 0) {
                                UUID uuid = (UUID) packet.getDataSingle();
                                return new ProxyPlayer(s, uuid);
                            }
                        }

                        return null;
                    }

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return true;
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
                        Optional<ProxyServer> server = ZulfBungeeSpigot.getPlugin().getConnection().getProxyServer(s);
                        return server.orElse(null);
                    }

                    @Override
                    public boolean canParse(@NotNull ParseContext context) {
                        return true;
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
