package tk.zulfengaming.zulfbungee.spigot.handlers;

import tk.zulfengaming.zulfbungee.universal.util.skript.ClientInfo;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class ClientInfoManager {

    private static final HashMap<String, ClientInfo> servers = new HashMap<>();

    public static ClientInfo getClientInfo(String nameIn) {
        return servers.get(nameIn);
    }

    public static void setServers(ProxyServer[] serverList) {

        servers.clear();

        for (ProxyServer server : serverList) {
            servers.put(server.getName(), server.getClientInfo());
        }

    }

    public static Collection<ProxyServer> getServers() {

        LinkedList<ProxyServer> serversOut = new LinkedList<>();

        for (String name : servers.keySet()) {
            serversOut.addLast(new ProxyServer(name));
        }

        return serversOut;

    }

}
