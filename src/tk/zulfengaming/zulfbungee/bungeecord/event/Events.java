package tk.zulfengaming.zulfbungee.bungeecord.event;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.*;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.CommandHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.task.tasks.CheckUpdateTask;
import tk.zulfengaming.zulfbungee.bungeecord.util.UpdateResult;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyPlayerDataContainer;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Events implements Listener {

    private final Server server;

    public Events(Server serverIn) {
        this.server = serverIn;
    }

    @EventHandler
    public void onServerConnected(ServerConnectedEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();

        if (eventPlayer.getServer() == null) {

            server.getPluginInstance().getTaskManager().newTask(() -> {

                ProxyServer proxyServer = new ProxyServer(event.getServer().getInfo().getName());
                ProxyPlayer playerOut = new ProxyPlayer(eventPlayer.getName(), eventPlayer.getUniqueId(), proxyServer);

                server.sendToAllClients(new Packet(PacketTypes.CONNECT_EVENT, false, true,
                        playerOut));

            }, event.toString());

        }

        if (eventPlayer.hasPermission("zulfen.admin") && eventPlayer.getServer() == null) {

            CheckUpdateTask updater = server.getPluginInstance().getUpdater();

            CompletableFuture.supplyAsync(updater)
                    .thenAccept(updateResult -> {

                        if (updateResult.isPresent()) {

                            UpdateResult getUpdaterResult = updateResult.get();

                            eventPlayer.sendMessage(new ComponentBuilder("A new update to ZulfBungee is available!")
                                    .color(ChatColor.AQUA)
                                    .append(" (Version " + getUpdaterResult.getLatestVersion() + ")")
                                    .italic(true)
                                    .color(ChatColor.YELLOW)
                                    .create());

                            eventPlayer.sendMessage(new ComponentBuilder("Click this link to get a direct download!")
                                    .color(ChatColor.DARK_AQUA)
                                    .underlined(true)
                                    .event(new ClickEvent(ClickEvent.Action.OPEN_URL, getUpdaterResult.getDownloadURL()))
                                    .create());
                        }

                    });

        }

    }

    @EventHandler
    public void onSwitchServerEvent(ServerSwitchEvent event) {

        ProxiedPlayer eventPlayer = event.getPlayer();

        if (event.getFrom() != null) {

            server.getPluginInstance().getTaskManager().newTask(() -> {

                String toName = eventPlayer.getServer().getInfo().getName();

                ProxyServer serverOut = new ProxyServer(toName);
                ProxyPlayer playerOut = new ProxyPlayer(eventPlayer.getName(), eventPlayer.getUniqueId(), serverOut);

                server.sendToAllClients(new Packet(PacketTypes.SERVER_SWITCH_EVENT, false, true, playerOut));

            }, event.toString());

        }

    }


    @EventHandler
    public void onServerKick(ServerKickEvent event) {

        if (event.getCause() == ServerKickEvent.Cause.SERVER) {

            server.getPluginInstance().getTaskManager().newTask(() -> {

                ProxiedPlayer player = event.getPlayer();

                String serverName = event.getKickedFrom().getName();

                if (server.getServerNames().contains(serverName)) {

                    ProxyPlayer playerOut = new ProxyPlayer(player.getName(), player.getUniqueId());

                    String legacyText = TextComponent.toLegacyText(event.getKickReasonComponent());

                    server.sendToAllClients(new Packet(PacketTypes.KICK_EVENT, false, true,
                            new ProxyPlayerDataContainer(legacyText, new ProxyPlayer[]{playerOut})));

                }

            }, event.toString());

        }

    }

    @EventHandler
    public void onPlayerDisconnect(PlayerDisconnectEvent event) {

        ProxiedPlayer player = event.getPlayer();

        if (player.getServer() != null) {

            server.getPluginInstance().getTaskManager().newTask(() -> {

                String serverName = player.getServer().getInfo().getName();

                if (server.getServerNames().contains(serverName)) {

                    server.sendToAllClients(new Packet(PacketTypes.DISCONNECT_EVENT, false, true,
                            new ProxyPlayer(player.getName(), player.getUniqueId())));

                }

            }, event.toString());

        }

    }
}
