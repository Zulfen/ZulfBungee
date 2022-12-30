package tk.zulfengaming.zulfbungee.universal;

import tk.zulfengaming.zulfbungee.universal.command.Constants;
import tk.zulfengaming.zulfbungee.universal.task.tasks.CheckUpdateTask;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.command.ProxyCommandSender;
import tk.zulfengaming.zulfbungee.universal.config.ProxyConfig;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ProxyPlayer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.ServerInfo;
import tk.zulfengaming.zulfbungee.universal.task.ProxyTaskManager;
import tk.zulfengaming.zulfbungee.universal.util.UpdateResult;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class ZulfBungeeProxy {

    private final CheckUpdateTask checkUpdateTask;

    protected ZulfBungeeProxy(CheckUpdateTask checkUpdateTask) {
        this.checkUpdateTask = checkUpdateTask;
    }

    public abstract void logDebug(String messageIn);

    public abstract void logInfo(String messageIn);

    public abstract void error(String messageIn);

    public abstract void warning(String messageIn);

    public abstract MainServer getServer();

    public abstract ProxyTaskManager getTaskManager();
    public abstract ProxyConfig getConfig();

    public abstract ProxyPlayer getPlayer(UUID uuidIn);
    public abstract ProxyPlayer getPlayer(String nameIn);
    public abstract Collection<ProxyPlayer> getPlayers();
    public abstract Map<String, ServerInfo> getServersCopy();

    public abstract String getVersion();

    public void checkUpdate(ProxyCommandSender senderIn, boolean notifySuccess) {

        CompletableFuture.supplyAsync(checkUpdateTask)
                .thenAccept(updateResult -> {

                    if (updateResult.isPresent()) {

                        UpdateResult getUpdaterResult = updateResult.get();

                        senderIn.sendMessage(String.format(Constants.MESSAGE_PREFIX + String.format("A new update to ZulfBungee is available! &e(%s)",
                                getUpdaterResult.getLatestVersion())));
                        senderIn.sendMessage(Constants.MESSAGE_PREFIX + "Copy this link into a browser for a direct download:");
                        senderIn.sendMessage(Constants.MESSAGE_PREFIX + String.format("&3&n%s", getUpdaterResult.getDownloadURL()));

                    } else if (notifySuccess) {

                        senderIn.sendMessage(String.format(Constants.MESSAGE_PREFIX + String.format("ZulfBungee is up to date! &e(%s)",
                                getVersion())));
                    }

                });

    }

}
