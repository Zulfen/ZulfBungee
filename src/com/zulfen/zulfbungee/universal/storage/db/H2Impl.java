package com.zulfen.zulfbungee.universal.storage.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zulfen.zulfbungee.universal.command.util.ChatColour;
import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.universal.storage.HikariSQLImpl;

import java.nio.file.Files;
import java.nio.file.Path;

public class H2Impl<P, T> extends HikariSQLImpl<P, T> {

    public H2Impl(MainServer<P, T> mainServerIn) {
        super(mainServerIn);
    }

    @Override
    protected HikariDataSource initialiseDataSource() {

        HikariConfig hikariConfig = new HikariConfig();

        String pathString;
        Path pluginFolder = getMainServer().getImpl().getPluginFolder();

        Path oldPath = pluginFolder.resolve(getDatabase() + ".db.mv.db");
        Path newPath = pluginFolder.resolve(getDatabase());

        // mistake with naming - .db suffix gets added automatically
        if (Files.exists(oldPath)) {
            getMainServer().getImpl().logDebug(ChatColour.YELLOW + "Using old database path format!");
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
