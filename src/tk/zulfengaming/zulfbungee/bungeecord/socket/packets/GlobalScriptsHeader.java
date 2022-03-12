package tk.zulfengaming.zulfbungee.bungeecord.socket.packets;

import tk.zulfengaming.zulfbungee.bungeecord.interfaces.PacketHandler;
import tk.zulfengaming.zulfbungee.bungeecord.socket.BaseServerConnection;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.socket.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.PacketTypes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.UUID;

public class GlobalScriptsHeader extends PacketHandler {

    private final LinkedHashMap<String, Long> scripts;

    public GlobalScriptsHeader(Server serverIn) {
        super(serverIn, PacketTypes.GLOBAL_SCRIPT_HEADER);
        this.scripts = serverIn.getPluginInstance().getConfig().getAvailableScripts();

    }

    @Override
    public Packet handlePacket(Packet packetIn, BaseServerConnection connection) {

        String scriptName = (String) packetIn.getDataSingle();

        getMainServer().getPluginInstance().getTaskManager().newTask(() -> {

            if (scripts.containsKey(scriptName)) {

                File scriptFile = getMainServer().getPluginInstance().getConfig().
                        getScriptsFolderPath().resolve(scriptName).toFile();

                byte[] buffer = new byte[4 * 1024];
                long totalRead = 0;

                try {

                    FileInputStream scriptStream = new FileInputStream(scriptFile);

                    long currentRead;
                    while ((currentRead = scriptStream.read(buffer)) != -1) {
                        totalRead += currentRead;
                        connection.send(new Packet(PacketTypes.GLOBAL_SCRIPT_DATA, false, true, buffer));
                    }

                    if (!(totalRead >= scriptFile.length())) {
                        getMainServer().getPluginInstance().logDebug("Script file " + scriptFile + " not sent properly?");
                        getMainServer().getPluginInstance().logDebug(totalRead + "/" + scriptFile.length());
                    }

                } catch (FileNotFoundException e) {
                    connection.getPluginInstance().error("Error opening script stream for: " +  scriptFile.getName());
                    e.printStackTrace();
                } catch (IOException e) {
                    connection.getPluginInstance().error("Error reading script stream for: " + scriptFile.getName());
                }

            }

        }, UUID.randomUUID().toString());


        return null;

    }

}
