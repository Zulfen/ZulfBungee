package com.zulfen.zulfbungee.core.socket;

import com.zulfen.zulfbungee.core.ZulfProxyImpl;
import com.zulfen.zulfbungee.core.command.ProxyCommandSender;
import com.zulfen.zulfbungee.core.handlers.proxy.ProxyCommHandler;
import com.zulfen.zulfbungee.core.interfaces.PacketConsumer;
import com.zulfen.zulfbungee.core.managers.MainServer;
import com.zulfen.zulfbungee.core.managers.PacketHandlerManager;
import com.zulfen.zulfbungee.core.socket.objects.Packet;
import com.zulfen.zulfbungee.core.socket.objects.PacketTypes;
import com.zulfen.zulfbungee.core.socket.objects.client.ClientPlayer;
import com.zulfen.zulfbungee.core.socket.objects.client.skript.ScriptAction;
import com.zulfen.zulfbungee.core.socket.objects.client.skript.ScriptInfo;
import com.zulfen.zulfbungee.core.socket.objects.proxy.ProxyEventPacket;
import com.zulfen.zulfbungee.core.socket.objects.proxy.ZulfProxyPlayer;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class ProxyServerConnection<P, T, C> implements PacketConsumer {

    protected final MainServer<P, T, C> mainServer;
    protected final ZulfProxyImpl<P, T, C> pluginInstance;
    protected final PacketHandlerManager<P, T, C> packetHandlerManager;

    protected ProxyCommHandler<P, T, C> proxyCommHandler;

    protected final AtomicBoolean connected = new AtomicBoolean(true);

    protected final SocketAddress socketAddress;

    public ProxyServerConnection(MainServer<P, T, C> mainServerIn, SocketAddress socketAddressIn) {
        this.mainServer = mainServerIn;
        this.pluginInstance = mainServer.getImpl();
        this.socketAddress = socketAddressIn;
        this.packetHandlerManager = new PacketHandlerManager<>(mainServerIn);
    }

    public void setProxyCommHandler(ProxyCommHandler<P, T, C> proxyCommHandler) {
        this.proxyCommHandler = proxyCommHandler;
    }

    public void start() {
        pluginInstance.getTaskManager().newTask(() -> proxyCommHandler.dataInLoop());
        pluginInstance.getTaskManager().newTask(() -> proxyCommHandler.dataOutLoop());
        pluginInstance.getTaskManager().newTask(() -> proxyCommHandler.processLoop());
    }

    public void sendProxyEventPacket(ProxyEventPacket packetIn) {
        boolean processCallback = packetIn.processCallback();
        if (processCallback) {
            sendDirect(packetIn);
        }
    }

    public void sendDirect(Packet packetIn) {
        assert proxyCommHandler != null : "Comm Handler is null!";
        proxyCommHandler.enqueuePacket(packetIn);
        pluginInstance.logDebug("Sent packet " + packetIn.getType() + "...");
    }

    // input null into senderIn to make the console reload the scripts, not a player.
    // name allows you to define a custom name if needed
    @SuppressWarnings("unchecked")

    public void sendScript(String scriptName, Path scriptPathIn, ScriptAction actionIn, ProxyCommandSender senderIn, boolean isLastScriptIn) {
        pluginInstance.getTaskManager().newTask(() -> {

            ClientPlayer playerOut = null;

            if (senderIn != null) {
                if (senderIn.isPlayer()) {
                    ZulfProxyPlayer<P, T, C> playerIn = (ZulfProxyPlayer<P, T, C>) senderIn;
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
    public boolean equals(Object o) {
        if (!(o instanceof ProxyServerConnection<?, ?, ?> that)) return false;
        return Objects.equals(mainServer, that.mainServer) && Objects.equals(pluginInstance, that.pluginInstance) && Objects.equals(packetHandlerManager, that.packetHandlerManager) && Objects.equals(proxyCommHandler, that.proxyCommHandler) && Objects.equals(connected, that.connected) && Objects.equals(socketAddress, that.socketAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mainServer, pluginInstance, packetHandlerManager, proxyCommHandler, connected, socketAddress);
    }

    @Override
    public void destroyConsumer() {
        destroy();
    }

    public SocketAddress getAddress() {
        return socketAddress;
    }

    public MainServer<P, T, C> getServer() {
        return mainServer;
    }

    public ZulfProxyImpl<P, T, C> getPluginInstance() {
        return pluginInstance;
    }

}
