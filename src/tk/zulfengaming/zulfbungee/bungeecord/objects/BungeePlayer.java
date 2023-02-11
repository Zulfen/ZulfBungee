package tk.zulfengaming.zulfbungee.bungeecord.objects;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;

import java.util.UUID;

public class BungeePlayer<P> extends ZulfProxyPlayer<ProxyServer> {

    private ZulfProxyServer<ProxyServer> server;

    private final String name;

    private final UUID uuid;

    private final ProxiedPlayer bungeePlayer;

    public BungeePlayer(ProxiedPlayer playerIn) {

        super(net.md_5.bungee.api.ProxyServer.getInstance());
        this.bungeePlayer = playerIn;
        this.name = playerIn.getName();
        this.uuid = playerIn.getUniqueId();

        ServerInfo serverInfo = bungeePlayer.getServer().getInfo();

        if (serverInfo != null) {
            this.server = new BungeeServer(serverInfo);
        }

    }

    public BungeePlayer(ProxiedPlayer playerIn, BungeeServer serverIn) {

        super(net.md_5.bungee.api.ProxyServer.getInstance());
        this.bungeePlayer = playerIn;
        this.name = playerIn.getName();
        this.uuid = playerIn.getUniqueId();
        this.server = serverIn;

    }

    private BaseComponent[] toComponent(String messageIn) {
        return TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes
                ('&', messageIn));
    }

    @Override
    public ZulfProxyServer<ProxyServer> getServer() {
        return server;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public UUID getUuid() {
        return uuid;
    }

    @Override
    public void connect(ZulfProxyServer<ProxyServer> serverIn) {
        ServerInfo serverInfo = platform.getServerInfo(serverIn.getName());
        if (serverInfo != null) {
            bungeePlayer.connect(serverInfo);
        }
    }

    @Override
    public void disconnect(String reason) {
        bungeePlayer.disconnect(toComponent(reason));
    }

    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public boolean hasPermission(String permission) {
        return bungeePlayer.hasPermission(permission);
    }

    @Override
    public void sendMessage(String message) {
        bungeePlayer.sendMessage(toComponent(message));
    }
}
