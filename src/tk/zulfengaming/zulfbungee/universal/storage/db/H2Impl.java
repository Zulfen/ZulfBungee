package tk.zulfengaming.zulfbungee.universal.storage.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import tk.zulfengaming.zulfbungee.universal.command.util.ChatColour;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.storage.HikariSQLImpl;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class H2Impl<P> extends HikariSQLImpl<P> {

    public H2Impl(MainServer<P> mainServerIn) {
        super(mainServerIn);
    }

    @Override
    protected HikariDataSource initialiseDataSource() {

        HikariConfig hikariConfig = new HikariConfig();

        String pathString;
        Path pluginFolder = getMainServer().getPluginInstance().getPluginFolder();

        Path oldPath = pluginFolder.resolve(getDatabase() + ".db.mv.db");
        Path newPath = pluginFolder.resolve(getDatabase());

        // mistake with naming - .db suffix gets added automatically
        if (Files.exists(oldPath)) {
            getMainServer().getPluginInstance().logDebug(ChatColour.YELLOW + "Using old database path format!");
            pathString = newPath.toAbsolutePath() + ".db";
        } else {
            pathString = newPath.toAbsolutePath().toString();
        }

        String jdbcUrl = "jdbc:h2:" + pathString + ";mode=MySQL";

        hikariConfig.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        hikariConfig.addDataSourceProperty("URL", jdbcUrl);
        hikariConfig.addDataSourceProperty("user", getUsername());
        hikariConfig.addDataSourceProperty("password", getPassword());

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        dataSource.setMaximumPoolSize(50);

        return dataSource;

    }
}
