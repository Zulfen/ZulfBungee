package tk.zulfengaming.zulfbungee.spigot.handlers;

import tk.zulfengaming.zulfbungee.universal.socket.ServerInfo;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.*;

public class ProxyServerInfoManager {

    private static final HashMap<String, ServerInfo> servers = new HashMap<>();

    public static ServerInfo getClientInfo(String nameIn) {
        return servers.get(nameIn);
    }

    public static void setServers(ProxyServer[] serverList) {
        servers.clear();
        Arrays.stream(serverList).forEach(server -> servers.put(server.getName(), server.getClientInfo()));
    }

    public static Collection<ProxyServer> getServers() {

        ArrayList<ProxyServer> serversOut = new ArrayList<>();

        for (String name : servers.keySet()) {
            serversOut.add(new ProxyServer(name));
        }

        return serversOut;

    }

}
