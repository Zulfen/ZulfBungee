package tk.zulfengaming.zulfbungee.velocity.objects;

import com.velocitypowered.api.proxy.server.RegisteredServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyServer;
import tk.zulfengaming.zulfbungee.velocity.ZulfVelocity;

public class VelocityServer implements ZulfProxyServer {

    private final String name;

    public VelocityServer(RegisteredServer velocityServerIn, ZulfVelocity pluginIn) {
        this.name = velocityServerIn.getServerInfo().getName();
    }

    @Override
    public String getName() {
        return name;
    }

}
