package tk.zulfengaming.zulfbungee.spigot.managers;

import tk.zulfengaming.zulfbungee.universal.socket.ServerInfo;
import tk.zulfengaming.zulfbungee.universal.util.skript.ProxyServer;

import java.util.*;
import java.util.stream.Collectors;

public class ProxyServerInfoManager {

    private static final HashMap<String, ServerInfo> servers = new HashMap<>();

    public static Optional<ProxyServer> toProxyServer(String nameIn) {

        ServerInfo serverInfo = servers.get(nameIn);

        if (serverInfo != null) {
            return Optional.of(new ProxyServer(nameIn, serverInfo));
        }

        return Optional.empty();

    }

    public static void setServers(ProxyServer[] serverList) {
        servers.clear();
        Arrays.stream(serverList).forEach(server -> servers.put(server.getName(), server.getServerInfo()));
    }

    public static boolean contains(String proxyServerNameIn) {
        return servers.containsKey(proxyServerNameIn);
    }

    public static List<ProxyServer> getServers() {
        return servers.keySet().stream().map(ProxyServer::new).collect(Collectors.toList());
    }

}
