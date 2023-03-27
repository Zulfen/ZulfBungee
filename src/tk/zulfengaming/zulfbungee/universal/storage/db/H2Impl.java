package tk.zulfengaming.zulfbungee.universal.storage.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.storage.HikariSQLImpl;

import java.io.File;

public class H2Impl<P> extends HikariSQLImpl<P> {

    public H2Impl(MainServer<P> mainServerIn) {
        super(mainServerIn);
    }

    @Override
    protected HikariDataSource initialiseDataSource() {

        HikariConfig hikariConfig = new HikariConfig();

        File path;

        File oldPath = new File(getMainServer().getPluginInstance().getPluginFolder(), getDatabase() + ".db");

        // mistake with naming - .db suffix gets added automatically
        if (oldPath.exists()) {
            path = oldPath;
        } else {
            path = new File(getMainServer().getPluginInstance().getPluginFolder(), getDatabase());
        }

        String jdbcUrl = "jdbc:h2:" + path.getAbsolutePath() + ";mode=MySQL";

        hikariConfig.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        hikariConfig.addDataSourceProperty("URL", jdbcUrl);
        hikariConfig.addDataSourceProperty("user", getUsername());
        hikariConfig.addDataSourceProperty("password", getPassword());

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        dataSource.setMaximumPoolSize(50);

        return dataSource;

    }
}
