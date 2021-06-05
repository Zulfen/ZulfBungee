package tk.zulfengaming.zulfbungee.bungeecord.storage.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.storage.SQLHandler;

import java.io.File;

public class H2Handler extends SQLHandler {

    public H2Handler(Server serverIn) {
        super(serverIn);
    }

    @Override
    public void initialise() {

        HikariConfig hikariConfig = new HikariConfig();

        File path = new File(getMainServer().getPluginInstance().getDataFolder(), getDatabase() + ".db");
        String jdbcUrl = "jdbc:h2:" + path.getAbsolutePath();

        hikariConfig.setDataSourceClassName("org.h2.jdbcx.JdbcDataSource");
        hikariConfig.addDataSourceProperty("URL", jdbcUrl);
        hikariConfig.addDataSourceProperty("user", getUsername());
        hikariConfig.addDataSourceProperty("password", getPassword());

        setDataSource(new HikariDataSource(hikariConfig));

        getDataSource().setMaximumPoolSize(10);

    }
}
