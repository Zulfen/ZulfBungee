package tk.zulfengaming.zulfbungee.universal.handlers.util;

public class UpdateResult {

    private final String latestVersion;
    private final String downloadURL;

    public UpdateResult(String latestVersionIn, String downloadURLIn) {
        this.latestVersion = latestVersionIn;
        this.downloadURL = downloadURLIn;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public String getDownloadURL() {
        return downloadURL;
    }
}
