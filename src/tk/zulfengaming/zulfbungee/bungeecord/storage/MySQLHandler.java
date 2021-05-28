package tk.zulfengaming.zulfbungee.bungeecord.storage;

import com.zaxxer.hikari.HikariDataSource;
import net.md_5.bungee.api.ChatColor;
import tk.zulfengaming.zulfbungee.bungeecord.interfaces.StorageImpl;
import tk.zulfengaming.zulfbungee.bungeecord.socket.Server;
import tk.zulfengaming.zulfbungee.universal.util.skript.NetworkVariable;
import tk.zulfengaming.zulfbungee.universal.util.skript.Value;

import java.sql.*;
import java.util.ArrayList;
import java.util.Optional;

public class MySQLHandler extends StorageImpl {

    private HikariDataSource dataSource;

    // TODO: Allow user customisation of the table name that this reads and writes to.

    public MySQLHandler(Server serverIn) {
        super(serverIn);

    }

    @Override
    public Optional<NetworkVariable> getVariables(String name) {

        try (java.sql.Connection tempConnection = dataSource.getConnection()) {

            if (name.endsWith("::*")) {

                String listName = name.split("::\\*")[0];

                PreparedStatement preparedStatement = tempConnection.prepareStatement("SELECT name, type, data FROM ? WHERE name LIKE ?");
                String finalisedQuery = "%" + listName + "::%";

                preparedStatement.setString(1, getTable());
                preparedStatement.setString(2, finalisedQuery);

                ResultSet result = preparedStatement.executeQuery();

                ArrayList<Value> values = new ArrayList<>();

                while (result.next()) {
                    String type = result.getString("type");
                    byte[] data = result.getBytes("data");

                    //getMainServer().getPluginInstance().logDebug("Got value " + valueName);
                    values.add(new Value(type, data));

                }

                if (!values.isEmpty()) {
                    return Optional.of(new NetworkVariable(name, null, values.toArray(new Value[0])));
                }

                return Optional.empty();

            } else {

                PreparedStatement preparedStatement = tempConnection.prepareStatement("SELECT data, type FROM ? WHERE name=?");

                preparedStatement.setString(1, getTable());
                preparedStatement.setString(2, name);

                ResultSet result = preparedStatement.executeQuery();

                if (result.next()) {

                    byte[] data = result.getBytes("data");
                    String type = result.getString("type");
                    Value value = new Value(type, data);

                    //getMainServer().getPluginInstance().logDebug("Got value " + name);

                    return Optional.ofNullable(new NetworkVariable(name, null, value));

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            getMainServer().getPluginInstance().error("Error fetching data from MySQL database!");
        }

        return Optional.empty();

    }

    @Override
    public void setVariables(NetworkVariable variable) {

        try (java.sql.Connection tempConnection = dataSource.getConnection()) {

            String variableNameIn = variable.getName();

            if (variableNameIn.endsWith("::*")) {
                Value[] variableValuesIn = variable.getValueArray();
                String variableNameInRoot = variableNameIn.split("::\\*")[0];

                for (int i = 0; i < variableValuesIn.length; i++) {
                    Value value = variableValuesIn[i];

                    PreparedStatement preparedStatement = tempConnection.prepareStatement("INSERT INTO ? (name, type, data) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE data=?, type=?");
                    String variableNameOut = variableNameInRoot + "::" + (i + 1);

                    preparedStatement.setString(1, getTable());
                    preparedStatement.setString(2, variableNameOut);
                    preparedStatement.setString(3, value.type);
                    preparedStatement.setBytes(4, value.data);

                    preparedStatement.setBytes(5, value.data);
                    preparedStatement.setString(6, value.type);

                    preparedStatement.executeUpdate();

                    //getMainServer().getPluginInstance().logDebug("Stored variable in list " + variableNameIn);

                }

            } else {

                Value value = variable.getSingleValue();

                PreparedStatement preparedStatement = tempConnection.prepareStatement("INSERT INTO ? (name, type, data) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE data=?, type=?");

                preparedStatement.setString(1, getTable());
                preparedStatement.setString(2, variableNameIn);
                preparedStatement.setString(3, value.type);
                preparedStatement.setBytes(4, value.data);

                preparedStatement.setBytes(5, value.data);
                preparedStatement.setString(6, value.type);

                preparedStatement.executeUpdate();

                //getMainServer().getPluginInstance().logDebug("Stored variable " + variableNameIn);

            }

        } catch (SQLException e) {
            e.printStackTrace();
            getMainServer().getPluginInstance().error("Error inserting value into MySQL database!");

        }
    }

    @Override
    public void addToVariable(String name, Value[] values) {
        try (java.sql.Connection tempConnection = dataSource.getConnection()) {

            if (name.endsWith("::*")) {

                String listName = name.split("::\\*")[0];

                PreparedStatement getStatement = tempConnection.prepareStatement("SELECT name, type, data FROM ? WHERE name LIKE ?");

                String finalisedQuery = "%" + listName + "::%";

                getStatement.setString(1, getTable());
                getStatement.setString(2, finalisedQuery);

                ResultSet result = getStatement.executeQuery();

                int querySize = 0;

                while (result.next()) {
                    querySize++;
                }

                int valueArrayIndex = 0;

                for (int i = 0; i < values.length; i++) {

                    int listIndex = querySize + (i + 1);

                    PreparedStatement setStatement = tempConnection.prepareStatement("INSERT INTO ? (name, type, data) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE data=?, type=?");
                    String variableNameOut = listName + "::" + listIndex;

                    setStatement.setString(1, getTable());
                    setStatement.setString(2, variableNameOut);
                    setStatement.setString(3, values[valueArrayIndex].type);
                    setStatement.setBytes(4, values[valueArrayIndex].data);

                    setStatement.setBytes(5, values[valueArrayIndex].data);
                    setStatement.setString(6, values[valueArrayIndex].type);

                    setStatement.executeUpdate();

                    valueArrayIndex++;

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            getMainServer().getPluginInstance().error("Error adding variable to MySQL Database!");
        }

    }

    @Override
    public void deleteVariables(String name) {

        try (java.sql.Connection tempConnection = dataSource.getConnection()) {

            PreparedStatement preparedStatement;

            if (name.endsWith("::*")) {

                preparedStatement = tempConnection.prepareStatement("DELETE FROM ? WHERE name LIKE ?");

                String listName = name.split("::\\*")[0];
                String finalisedQuery = "%" + listName + "::%";

                preparedStatement.setString(1, getTable());
                preparedStatement.setString(2, finalisedQuery);

            } else {
                preparedStatement = tempConnection.prepareStatement("DELETE FROM variables WHERE name=?");
                preparedStatement.setString(1, name);

            }
            preparedStatement.executeUpdate();

            //getMainServer().getPluginInstance().logDebug("Deleted variable " + name);


        } catch (SQLException e) {

            e.printStackTrace();
            getMainServer().getPluginInstance().error("Error deleting value from MySQL database!");

        }
    }

    @Override
    public void removeFromVariable(String name, Value[] values) {

    }

    @Override
    public void shutdown() {

        getMainServer().getPluginInstance().logDebug("Shutting down MySQL connection...");
        dataSource.close();

    }

    @Override
    public void initialise() {

        dataSource = new HikariDataSource();
        dataSource.setMaximumPoolSize(10);

        String jdbcUrl = "jdbc:mysql://" + getHost() + ":" + getPort() + "/" + getDatabase();

        if (!getMainServer().getPluginInstance().getConfig().getBoolean("mysql-useSSL")) {
            jdbcUrl += "?&useSSL=false";
        }

        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(getUsername());
        dataSource.setPassword(getPassword());

        try (java.sql.Connection tempConnection = dataSource.getConnection()) {

            getMainServer().getPluginInstance().logInfo(ChatColor.GREEN + "MySQL connected successfully to " + jdbcUrl);

            DatabaseMetaData metaData = tempConnection.getMetaData();

            ResultSet resultSet = metaData.getTables(null, null, getTable(), null);
            if (!resultSet.next()) {

                getMainServer().getPluginInstance().logInfo("Setting up your database...");

                String creationStatement = "CREATE TABLE " + getTable() + " " +
                        "(name VARCHAR(255) not NULL PRIMARY KEY, " +
                        " type VARCHAR(128) not NULL,  " +
                        " data VARBINARY(8000))";

                Statement statement = tempConnection.createStatement();
                statement.execute(creationStatement);

                getMainServer().getPluginInstance().logInfo(ChatColor.GREEN + "Done setting up the MySQL database!");

            }

        } catch (SQLException e) {
            getMainServer().getPluginInstance().error("There was an error setting up/connecting to the MySQL database!");
            e.printStackTrace();
        }

    }

}
