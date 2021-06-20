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

        boolean useSSL = getMainServer().getPluginInstance().getConfig().getBoolean("mysql-useSSL");
        boolean verifyCertificate = getMainServer().getPluginInstance().getConfig().getBoolean("mysql-verifyCertificate");

        if (!useSSL) {

            jdbcUrl += "?&useSSL=false";

        } else if (!verifyCertificate) {

            jdbcUrl += "&verifyServerCertificate=false";

        } else {

            jdbcUrl += "&verifyServerCertificate=true";

        }

        getDataSource().setJdbcUrl(jdbcUrl);
        getDataSource().setUsername(getUsername());
        getDataSource().setPassword(getPassword());

    }
}
