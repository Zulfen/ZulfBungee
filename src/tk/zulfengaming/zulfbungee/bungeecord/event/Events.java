package tk.zulfengaming.zulfbungee.bungeecord.event;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.utilclasses.skript.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.utilclasses.skript.ProxyServer;

public class Events implements Listener {

    private Server server;

    public Events(Server serverIn) {
        this.server = serverIn;
    }

    @EventHandler
    public void onServerSwitch(ServerSwitchEvent event) {

        ProxiedPlayer player = event.getPlayer();

        String serverName = player.getServer().getInfo().getName();
        ProxyPlayer playerOut = new ProxyPlayer(player.getName(), player.getUniqueId(), new ProxyServer(serverName));

        server.sendToAllClients(new Packet(PacketTypes.SWITCH_SERVER_EVENT, false, true, playerOut));

    }
}
