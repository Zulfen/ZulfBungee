package tk.zulfengaming.zulfbungee.spigot.handlers;

import tk.zulfengaming.zulfbungee.universal.socket.ServerInfo;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;

public class ClientInfoManager {

    private static final HashMap<String, ServerInfo> servers = new HashMap<>();

    public static ServerInfo getClientInfo(String nameIn) {
        return servers.get(nameIn);
    }

    public static void setServers(ProxyServer[] serverList) {
        servers.clear();
        Arrays.stream(serverList).forEach(server -> servers.put(server.getName(), server.getClientInfo()));
    }

    public static Collection<ProxyServer> getServers() {

        LinkedList<ProxyServer> serversOut = new LinkedList<>();

        for (String name : servers.keySet()) {
            serversOut.addLast(new ProxyServer(name));
        }

        return serversOut;

    }

}
