package tk.zulfengaming.zulfbungee.spigot.handlers;

import tk.zulfengaming.zulfbungee.universal.socket.ServerInfo;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.*;
import java.util.stream.Collectors;

public class ProxyServerInfoManager {

    private static final HashMap<String, ServerInfo> servers = new HashMap<>();

    public static ServerInfo getClientInfo(String nameIn) {
        return servers.get(nameIn);
    }

    public static ProxyServer toProxyServer(String nameIn) {
        return new ProxyServer(nameIn, servers.get(nameIn));
    }

    public static void setServers(ProxyServer[] serverList) {
        servers.clear();
        Arrays.stream(serverList).forEach(server -> servers.put(server.getName(), server.getClientInfo()));
    }

    public static boolean contains(String proxyServerNameIn) {
        return servers.containsKey(proxyServerNameIn);
    }

    public static List<ProxyServer> getServers() {
        return servers.keySet().stream().map(ProxyServer::new).collect(Collectors.toList());
    }

}
