package tk.zulfengaming.zulfbungee.bungeecord.event;

import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.socket.ServerConnection;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.utilclasses.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.utilclasses.skript.ProxyServer;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;

public class Events implements Listener {

    private final Server server;

    public Events(Server serverIn) {
        this.server = serverIn;
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {

        ProxiedPlayer player = event.getPlayer();

        server.getPluginInstance().getTaskManager().newTask(() -> {

            String serverName = null;

            for (SocketAddress storedSockAddr : server.getServerConnections().keySet()) {
                InetAddress storedInetAddr = ((InetSocketAddress) storedSockAddr).getAddress();

                for (ServerInfo info : server.getPluginInstance().getProxy().getServersCopy().values()) {

                    InetAddress retrievedInetAddr = ((InetSocketAddress) info.getSocketAddress()).getAddress();

                    if (retrievedInetAddr.equals(storedInetAddr)) {

                        ServerConnection connection = server.getServerConnections().get(storedSockAddr);
                        serverName = server.getActiveConnections().inverse().get(connection);

                    }
                }
            }

            ProxyPlayer playerOut = new ProxyPlayer(player.getName(), player.getUniqueId(), new ProxyServer(serverName));

            server.sendToAllClients(new Packet(PacketTypes.SWITCH_SERVER_EVENT, false, true, playerOut));

        }, event.toString());

    }
}
