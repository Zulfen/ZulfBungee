package tk.zulfengaming.zulfbungee.bungeecord.storage.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import tk.zulfengaming.zulfbungee.bungeecord.socket.MainServer;
import tk.zulfengaming.zulfbungee.bungeecord.storage.HikariSQLImpl;

import java.io.File;

public class H2Impl extends HikariSQLImpl {

    public H2Impl(MainServer mainServerIn) {
        super(mainServerIn);
    }

    @Override
    protected HikariDataSource initialiseDataSource() {

        HikariConfig hikariConfig = new HikariConfig();

        File path = new File(getMainServer().getPluginInstance().getDataFolder(), getDatabase() + ".db");
        String jdbcUrl = "jdbc:h2:" + path.getAbsolutePath() + ";mode=MySQL";

        hikariConfig.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        hikariConfig.addDataSourceProperty("URL", jdbcUrl);
        hikariConfig.addDataSourceProperty("user", getUsername());
        hikariConfig.addDataSourceProperty("password", getPassword());

        HikariDataSource dataSource = new HikariDataSource(hikariConfig);

        dataSource.setMaximumPoolSize(10);

        return dataSource;

    }
}
