package tk.zulfengaming.zulfbungee.universal.config;

import java.nio.file.Path;
import java.util.List;

public interface ProxyConfig {

    List<String> getScripts();

    Path getScriptsFolderPath();

    Path getScriptPath(String scriptNameIn);

    String getString(String node);

    boolean getBoolean(String node);

    int getInt(String node);

    List<Integer> getIntList(String node);

}
