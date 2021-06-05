package tk.zulfengaming.zulfbungee.bungeecord.storage.db;

import com.zaxxer.hikari.HikariDataSource;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.storage.SQLHandler;

public class MySQLHandler extends SQLHandler {

    public MySQLHandler(Server serverIn) {
        super(serverIn);
    }

    @Override
    public void initialise() {

        setDataSource(new HikariDataSource());

        getDataSource().setMaximumPoolSize(10);

        String jdbcUrl = "jdbc:mysql://" + getHost() + ":" + getPort() + "/" + getDatabase();

        if (!getMainServer().getPluginInstance().getConfig().getBoolean("mysql-useSSL")) {
            jdbcUrl += "?&useSSL=false";
        }

        getDataSource().setJdbcUrl(jdbcUrl);
        getDataSource().setUsername(getUsername());
        getDataSource().setPassword(getPassword());

    }
}
