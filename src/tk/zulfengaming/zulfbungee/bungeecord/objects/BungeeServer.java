package tk.zulfengaming.zulfbungee.bungeecord.objects;

import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;

public class BungeeServer implements ZulfProxyServer {

    private final String name;

    public BungeeServer(net.md_5.bungee.api.config.ServerInfo bungeeInfoIn) {
        this.name = bungeeInfoIn.getName();
    }

    @Override
    public String getName() {
        return name;
    }

}
