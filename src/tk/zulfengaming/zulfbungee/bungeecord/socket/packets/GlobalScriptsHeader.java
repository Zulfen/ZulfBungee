package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

public class GlobalScriptsHeader extends PacketHandler {

    public GlobalScriptsHeader(Server serverIn) {
        super(serverIn, PacketTypes.GLOBAL_SCRIPT_HEADER);

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection connection) {

        String scriptName = (String) packetIn.getDataSingle();

        getMainServer().getPluginInstance().getTaskManager().newTask(() -> {

            if (getMainServer().getPluginInstance().getConfig().getScriptNames().contains(scriptName)) {

                Path scriptPath = getMainServer().getPluginInstance().getConfig().
                        getScriptsFolderPath().resolve(scriptName);

                try {

                    byte[] data = Files.readAllBytes(scriptPath);
                    Byte[] dataAsObj = new Byte[data.length];

                    int i = 0;

                    // bungeecord doesn't come bundled with apache commons, so just doing byte autoboxing manually
                    for(byte b: data)
                        dataAsObj[i++] = b;  // Autoboxing.

                    connection.send(new Packet(PacketTypes.GLOBAL_SCRIPT_DATA,  false, true, dataAsObj));

                } catch (IOException e) {
                    getMainServer().getPluginInstance().error(String.format("Error while parsing script %s!", scriptName));
                    e.printStackTrace();
                }

            } else {
                // sends an empty array if it doesn't exist for whatever reason
                connection.send(new Packet(PacketTypes.GLOBAL_SCRIPT_DATA,  false, true, new Object[0]));
            }

        }, UUID.randomUUID().toString());


        return null;

    }

}
