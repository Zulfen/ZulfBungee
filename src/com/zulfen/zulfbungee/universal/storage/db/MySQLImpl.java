package com.zulfen.zulfbungee.universal.storage.db;

import com.zaxxer.hikari.HikariDataSource;
import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.universal.storage.HikariSQLImpl;

public class MySQLImpl<P, T> extends HikariSQLImpl<P, T> {

    public MySQLImpl(MainServer<P, T> mainServerIn) {
        super(mainServerIn);
    }

    @Override
    protected HikariDataSource initialiseDataSource() {

        HikariDataSource dataSource = new HikariDataSource();

        dataSource.setMaximumPoolSize(10);

        String jdbcUrl = "jdbc:mysql://" + getHost() + ":" + getPort() + "/" + getDatabase();

        boolean useSSL = getMainServer().getImpl().getConfig().getBoolean("mysql-use-ssl");
        boolean verifyCertificate = getMainServer().getImpl().getConfig().getBoolean("mysql-verify-certificate");

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

        return dataSource;

    }
}
