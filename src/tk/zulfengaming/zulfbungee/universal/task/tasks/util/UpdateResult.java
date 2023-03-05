package tk.zulfengaming.zulfbungee.universal.task.tasks.util;

public class UpdateResult {

    private final String latestVersion;
    private final String downloadURL;
    private final VersionStatus versionStatus;

    public UpdateResult(String latestVersionIn, String downloadURLIn, VersionStatus versionStatusIn) {
        this.latestVersion = latestVersionIn;
        this.downloadURL = downloadURLIn;
        this.versionStatus = versionStatusIn;
    }

    public String getLatestVersion() {
        return latestVersion;
    }

    public String getDownloadURL() {
        return downloadURL;
    }

    public VersionStatus getVersionStatus() {
        return versionStatus;
    }

}
