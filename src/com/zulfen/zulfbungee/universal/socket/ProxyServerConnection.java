package com.zulfen.zulfbungee.universal.socket;

import com.zulfen.zulfbungee.universal.ZulfProxyImpl;
import com.zulfen.zulfbungee.universal.command.ProxyCommandSender;
import com.zulfen.zulfbungee.universal.handlers.proxy.ProxyCommHandler;
import com.zulfen.zulfbungee.universal.interfaces.PacketConsumer;
import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.universal.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.universal.socket.objects.Packet;
import com.zulfen.zulfbungee.universal.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.universal.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ScriptAction;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.ScriptInfo;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.EventPacket;
import com.zulfen.zulfbungee.universal.socket.objects.proxy.ZulfProxyPlayer;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ProxyServerConnection<P, T> implements PacketConsumer {

    protected final MainServer<P, T> mainServer;
    protected final ZulfProxyImpl<P, T> pluginInstance;
    protected final PacketHandlerManager<P, T> packetHandlerManager;

    protected ProxyCommHandler<P, T> proxyCommHandler;

    protected final AtomicBoolean connected = new AtomicBoolean(true);

    protected final SocketAddress socketAddress;

    public ProxyServerConnection(MainServer<P, T> mainServerIn, SocketAddress socketAddressIn) {
        this.mainServer = mainServerIn;
        this.pluginInstance = mainServer.getImpl();
        this.socketAddress = socketAddressIn;
        this.packetHandlerManager = new PacketHandlerManager<>(mainServerIn);
    }

    public void setProxyCommHandler(ProxyCommHandler<P, T> proxyCommHandler) {
        this.proxyCommHandler = proxyCommHandler;
    }

    public void start() {
        pluginInstance.getTaskManager().newTask(() -> proxyCommHandler.dataInLoop());
        pluginInstance.getTaskManager().newTask(() -> proxyCommHandler.dataOutLoop());
        pluginInstance.getTaskManager().newTask(() -> proxyCommHandler.processLoop());
    }

    public void sendEventPacket(EventPacket packetIn) {
        boolean processCallback = packetIn.processCallback();
        if (processCallback) {
            sendDirect(packetIn);
        }
    }

    public synchronized void sendDirect(Packet packetIn) {
        assert proxyCommHandler != null : "Comm Handler is null!";
        proxyCommHandler.offerPacket(packetIn);
        pluginInstance.logDebug("Sent packet " + packetIn.getType() + "...");
    }

    // input null into senderIn to make the console reload the scripts, not a player.
    // name allows you to define a custom name if needed
    public void sendScript(String scriptName, Path scriptPathIn, ScriptAction actionIn, ProxyCommandSender<P, T> senderIn, boolean isLastScriptIn) {

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
                            scriptName, playerOut, data, isLastScriptIn)));

                } else {
                    sendDirect(new Packet(PacketTypes.GLOBAL_SCRIPT, false, true, new ScriptInfo(ScriptAction.DELETE,
                            scriptName, playerOut, new byte[0], isLastScriptIn)));
                }


            } catch (IOException e) {
                pluginInstance.error(String.format("Error while parsing script %s!", scriptName));
                e.printStackTrace();
            }

        });

    }

    public void consume(Packet packetIn) {

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

    public void destroy() {
        assert proxyCommHandler != null : "Comm Handler is null!";
        if (connected.compareAndSet(true, false)) {
            proxyCommHandler.destroy();
            mainServer.removeServerConnection(this);
        }
    }

    @Override
    public void destroyConsumer() {
        destroy();
    }

    public SocketAddress getAddress() {
        return socketAddress;
    }

    public MainServer<P, T> getServer() {
        return mainServer;
    }

    public ZulfProxyImpl<P, T> getPluginInstance() {
        return pluginInstance;
    }

}
