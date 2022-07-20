package tk.zulfengaming.zulfbungee.bungeecord.storage.db;

import com.zaxxer.hikari.HikariDataSource;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.bungeecord.storage.HikariSQLImpl;

public class MySQLImpl extends HikariSQLImpl {

    public MySQLImpl(Server serverIn) {
        super(serverIn);
    }

    @Override
    public void initialise() {

        dataSource = new HikariDataSource();

        dataSource.setMaximumPoolSize(10);

        String jdbcUrl = "jdbc:mysql://" + getHost() + ":" + getPort() + "/" + getDatabase();

        boolean useSSL = getMainServer().getPluginInstance().getConfig().getBoolean("mysql-use-ssl");
        boolean verifyCertificate = getMainServer().getPluginInstance().getConfig().getBoolean("mysql-verify-certificate");

        if (!useSSL) {

            jdbcUrl += "?&useSSL=false";

        } else if (!verifyCertificate) {

            jdbcUrl += "?&useSSL=true&verifyServerCertificate=false";

        } else {

            jdbcUrl += "?&useSSL=true&verifyServerCertificate=true";

        }

        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(getUsername());
        dataSource.setPassword(getPassword());

    }
}
