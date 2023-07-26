package tk.zulfengaming.zulfbungee.universal.socket;

import tk.zulfengaming.zulfbungee.universal.ZulfBungeeProxy;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.handlers.ProxyCommHandler;
import tk.zulfengaming.zulfbungee.universal.managers.PacketHandlerManager;
import tk.zulfengaming.zulfbungee.universal.managers.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.Packet;
import tk.zulfengaming.zulfbungee.universal.socket.objects.PacketTypes;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.ClientPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptAction;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.ScriptInfo;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.EventPacket;
import tk.zulfengaming.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ProxyServerConnection<P, T> implements Runnable {

    protected final MainServer<P, T> mainServer;
    protected final ZulfBungeeProxy<P, T> pluginInstance;
    protected final PacketHandlerManager<P, T> packetHandlerManager;

    protected ProxyCommHandler<P, T> proxyCommHandler;

    protected final AtomicBoolean connected = new AtomicBoolean(true);

    protected final SocketAddress socketAddress;

    public ProxyServerConnection(MainServer<P, T> mainServerIn, SocketAddress socketAddressIn) {
        this.mainServer = mainServerIn;
        this.pluginInstance = mainServer.getPluginInstance();
        this.socketAddress = socketAddressIn;
        this.packetHandlerManager = new PacketHandlerManager<>(mainServerIn);
    }

    @Override
    public void run() {

        assert proxyCommHandler != null : "Comm Handler is null!";

        while (connected.get()) {
            Optional<Packet> read = proxyCommHandler.readPacket();
            read.ifPresent(this::processPacket);
        }

    }

    public void sendDirect(Packet packetIn) {
        assert proxyCommHandler != null : "Comm Handler is null!";
        proxyCommHandler.send(packetIn);
        if (packetIn.getType() != PacketTypes.HEARTBEAT_PROXY) {
            pluginInstance.logDebug("Sent packet " + packetIn.getType() + "...");
        }
    }

    public void destroy() {
        assert proxyCommHandler != null : "Comm Handler is null!";
        if (connected.compareAndSet(true, false)) {
            proxyCommHandler.destroy();
            mainServer.removeServerConnection(this);
        }
    }

    // input null into senderIn to make the console reload the scripts, not a player.
    // name allows you to define a custom name if needed
    public void sendScript(String scriptName, Path scriptPathIn, ScriptAction actionIn, ProxyCommandSender<P, T> senderIn) {

        pluginInstance.getTaskManager().newTask(() -> {

            ClientPlayer playerOut = null;

            if (senderIn != null) {
                if (senderIn.isPlayer()) {
                    ZulfProxyPlayer<P, T> playerIn = (ZulfProxyPlayer<P, T>) senderIn;
                    playerOut = new ClientPlayer(playerIn.getName(), playerIn.getUuid());
                }
            }

            try {

                if (actionIn != ScriptAction.DELETE) {

                    byte[] data = Files.readAllBytes(scriptPathIn);

                    sendDirect(new Packet(PacketTypes.GLOBAL_SCRIPT, false, true, new ScriptInfo(actionIn,
                            scriptName, playerOut, data)));

                } else {
                    sendDirect(new Packet(PacketTypes.GLOBAL_SCRIPT, false, true, new ScriptInfo(ScriptAction.DELETE,
                            scriptName, playerOut, new byte[0])));
                }


            } catch (IOException e) {
                pluginInstance.error(String.format("Error while parsing script %s!", scriptName));
                e.printStackTrace();
            }

        });

    }

    protected void processPacket(Packet packetIn) {

        try {

            Packet handledPacket = packetHandlerManager.handlePacket(packetIn, this);

            if (packetIn.isReturnable() && handledPacket != null) {
                sendDirect(handledPacket);
            }

        } catch (Exception e) {

            // Used if unhandled exception occurs
            pluginInstance.error(String.format("Unhandled exception occurred in connection with address %s", getAddress()));
            e.printStackTrace();

            destroy();

        }

    }

    public void setProxyCommHandler(ProxyCommHandler<P, T> proxyCommHandlerIn) {
        this.proxyCommHandler = proxyCommHandlerIn;
        proxyCommHandler.setServerConnection(this);
    }

    public SocketAddress getAddress() {
        return socketAddress;
    }

    public MainServer<P, T> getServer() {
        return mainServer;
    }

    public ZulfBungeeProxy<P, T> getPluginInstance() {
        return pluginInstance;
    }

}
