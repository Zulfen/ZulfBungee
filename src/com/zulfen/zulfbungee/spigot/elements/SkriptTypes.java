package com.zulfen.zulfbungee.spigot.elements;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.classes.Serializer;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import ch.njol.yggdrasil.Fields;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientInfo;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientServer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.NetworkVariable;
import org.jetbrains.annotations.NotNull;

import java.io.StreamCorruptedException;
import java.util.UUID;

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

                }).serializer(new Serializer<ClientPlayer>() {

                    @Override
                    public @NotNull Fields serialize(ClientPlayer clientPlayer) {
                        Fields fields = new Fields();
                        fields.putObject("name", clientPlayer.getName());
                        fields.putObject("uuid", clientPlayer.getUuid());
                        return fields;
                    }

                    @Override
                    public void deserialize(ClientPlayer o, Fields f) {
                        assert false;
                    }

                    @Override
                    protected ClientPlayer deserialize(@NotNull Fields fields) throws StreamCorruptedException {
                        String name = fields.getObject("name", String.class);
                        UUID uuid = fields.getObject("uuid", UUID.class);
                        return new ClientPlayer(name, uuid);
                    }

                    @Override
                    public boolean mustSyncDeserialization() {
                        return false;
                    }

                    @Override
                    protected boolean canBeInstantiated() {
                        return false;
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

                }).serializer(new Serializer<ClientServer>() {

                    @Override
                    public @NotNull Fields serialize(ClientServer clientServer) {
                        Fields fields = new Fields();
                        ClientInfo clientInfo = clientServer.getClientInfo();
                        fields.putObject("name", clientServer.getName());
                        fields.putPrimitive("maxplayers", clientInfo.getMaxPlayers());
                        fields.putPrimitive("minecraftport", clientInfo.getMinecraftPort());
                        fields.putObject("versionstring", clientInfo.getVersionString());
                        return fields;
                    }

                    @Override
                    public void deserialize(ClientServer o, Fields f) {
                        assert false;
                    }

                    @Override
                    protected ClientServer deserialize(@NotNull Fields fields) throws StreamCorruptedException {
                        String name = fields.getObject("name", String.class);
                        Integer maxplayers = fields.getPrimitive("maxplayers", Integer.class);
                        Integer minecraftPort = fields.getPrimitive("minecraftport", Integer.class);
                        String versionString = fields.getObject("versionstring", String.class);
                        ClientInfo clientInfo = new ClientInfo(maxplayers, minecraftPort, versionString);
                        return new ClientServer(name, clientInfo);
                    }

                    @Override
                    public boolean mustSyncDeserialization() {
                        return false;
                    }

                    @Override
                    protected boolean canBeInstantiated() {
                        return false;
                    }

                }));

        Classes.registerClass(new ClassInfo<>(NetworkVariable.class, "networkvariable")
                .user("networkvariables?")
                .name("Network Variable")
                .description("Represents a network variable.")
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
