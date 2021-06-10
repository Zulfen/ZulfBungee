package tk.zulfengaming.zulfbungee.spigot.handlers;

import tk.zulfengaming.zulfbungee.universal.util.skript.ClientInfo;

import java.util.HashMap;

public class ClientInfoManager {

    private static final HashMap<String, ClientInfo> servers = new HashMap<>();

    public static ClientInfo getClientInfo(String nameIn) {
        return servers.get(nameIn);
    }

    public static void addClientInfo(String nameIn, ClientInfo clientInfoIn) {
        servers.put(nameIn, clientInfoIn);
    }

    public static void removeClientInfo(String nameIn, ClientInfo clientInfoIn) {
        servers.remove(nameIn, clientInfoIn);
    }

}
