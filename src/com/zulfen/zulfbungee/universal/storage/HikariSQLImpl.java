package com.zulfen.zulfbungee.universal.storage;

import com.zaxxer.hikari.HikariDataSource;
import com.zulfen.zulfbungee.universal.command.util.ChatColour;
import com.zulfen.zulfbungee.universal.managers.MainServer;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.NetworkVariable;
import com.zulfen.zulfbungee.universal.socket.objects.client.skript.Value;
import com.zulfen.zulfbungee.universal.storage.util.variable.UniversalVariableUtils;
import org.jetbrains.annotations.NotNull;
import com.zulfen.zulfbungee.universal.interfaces.StorageImpl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public abstract class HikariSQLImpl<P, T> extends StorageImpl<P, T> {

    private final HikariDataSource dataSource;
    private final boolean caseInsensitive;

    public HikariSQLImpl(MainServer<P, T> mainServerIn) {
        super(mainServerIn);
        this.dataSource = initialiseDataSource();
        this.caseInsensitive = mainServerIn.getImpl().getConfig().getBoolean("case-insensitive-variables");
    }

    protected abstract HikariDataSource initialiseDataSource();

    @Override
    public void setupDatabase() {

        try (java.sql.Connection tempConnection = dataSource.getConnection()) {

            getMainServer().getImpl().logInfo(ChatColour.GREEN + "Storage successfully started!");

            String creationStatement = "CREATE TABLE IF NOT EXISTS variables " +
                    "(name VARCHAR(255) not NULL PRIMARY KEY, " +
                    " type VARCHAR(128) not NULL,  " +
                    " data VARBINARY(8000))";

            Statement statement = tempConnection.createStatement();
            statement.execute(creationStatement);

            getMainServer().getImpl().logInfo(ChatColour.GREEN + "Done setting up the database!");


        } catch (SQLException e) {
            getMainServer().getImpl().error("There was an error setting up/connecting to the database!");
            e.printStackTrace();
        }

    }

    @Override
    public Optional<NetworkVariable> getVariable(@NotNull String name) {

        try (java.sql.Connection tempConnection = dataSource.getConnection()) {

            String nameToRetrieve;
            if (caseInsensitive) {
                nameToRetrieve = name.toLowerCase();
            } else {
                nameToRetrieve = name;
            }

            if (nameToRetrieve.endsWith("::*")) {

                String listName = nameToRetrieve.split("::\\*")[0];
                String sqlStatement;

                if (caseInsensitive) {
                    sqlStatement = "SELECT type, data " +
                            "FROM variables " +
                            "WHERE LOWER(name) LIKE LOWER(?)";
                } else {
                    sqlStatement = "SELECT type, data FROM variables WHERE name LIKE ?";
                }

                PreparedStatement preparedStatement = tempConnection.prepareStatement(sqlStatement);
                String finalisedQuery = "%" + listName + "::%";

                preparedStatement.setString(1, finalisedQuery);
                ResultSet result = preparedStatement.executeQuery();

                ArrayList<Value> values = new ArrayList<>();

                while (result.next()) {
                    String type = result.getString("type");
                    byte[] data = result.getBytes("data");
                    values.add(new Value(type, data));
                }

                if (!values.isEmpty()) {
                    return Optional.of(new NetworkVariable(name, null, values.toArray(new Value[0])));
                }

                return Optional.empty();

            } else {

                String sqlStatement;
                if (caseInsensitive) {
                    sqlStatement = "SELECT name, data, type " +
                            "FROM variables " +
                            "WHERE LOWER(name) = LOWER(?)";
                } else {
                    sqlStatement = "SELECT data, type FROM variables WHERE name=?";
                }

                PreparedStatement preparedStatement = tempConnection.prepareStatement(sqlStatement);

                preparedStatement.setString(1, nameToRetrieve);

                ResultSet result = preparedStatement.executeQuery();

                if (result.next()) {

                    byte[] data = result.getBytes("data");
                    String type = result.getString("type");
                    Value value = new Value(type, data);

                    return Optional.of(new NetworkVariable(name, value));

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            getMainServer().getImpl().error("Error fetching data from MySQL database!");
        }

        return Optional.empty();

    }

    @Override
    public void setVariable(@NotNull NetworkVariable variable) {

        try (java.sql.Connection tempConnection = dataSource.getConnection()) {

            String variableNameIn = variable.getName();

            String nameToRetrieve;
            if (caseInsensitive) {
                nameToRetrieve = variableNameIn.toLowerCase();
            } else {
                nameToRetrieve = variableNameIn;
            }


            if (nameToRetrieve.endsWith("::*")) {
                Value[] variableValuesIn = variable.getValueArray();
                String variableNameInRoot = nameToRetrieve.split("::\\*")[0];

                for (int i = 0; i < variableValuesIn.length; i++) {

                    Value value = variableValuesIn[i];

                    PreparedStatement preparedStatement = tempConnection.prepareStatement("INSERT INTO variables (name, type, data) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE data=?, type=?");
                    String variableNameOut = variableNameInRoot + "::" + (i + 1);

                    preparedStatement.setString(1, variableNameOut);
                    preparedStatement.setString(2, value.type);
                    preparedStatement.setBytes(3, value.data);

                    preparedStatement.setBytes(4, value.data);
                    preparedStatement.setString(5, value.type);

                    preparedStatement.executeUpdate();

                    //getMainServer().getImpl().logDebug("Stored variable in list " + variableNameIn);

                }

            } else {

                if (variable.getValueArray().length > 0) {

                    Value value = variable.getSingleValue();

                    PreparedStatement preparedStatement = tempConnection.prepareStatement("INSERT INTO variables (name, type, data) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE data=?, type=?");

                    preparedStatement.setString(1, nameToRetrieve);
                    preparedStatement.setString(2, value.type);
                    preparedStatement.setBytes(3, value.data);

                    preparedStatement.setBytes(4, value.data);
                    preparedStatement.setString(5, value.type);

                    preparedStatement.executeUpdate();

                    //getMainServer().getImpl().logDebug("Stored variable " + variableNameIn);

                } else {
                    getMainServer().getImpl().logDebug(String.format("%sVariable %s appears to be empty? Unable to save.", ChatColour.YELLOW, variableNameIn));
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
            getMainServer().getImpl().error("Error inserting value into MySQL database!");

        }
    }

    @Override
    public void addToVariable(@NotNull String name, Value[] values) {

        try (java.sql.Connection tempConnection = dataSource.getConnection()) {

            String nameToRetrieve;
            if (caseInsensitive) {
                nameToRetrieve = name.toLowerCase();
            } else {
                nameToRetrieve = name;
            }

            if (name.endsWith("::*")) {

                String listName = nameToRetrieve.split("::\\*")[0];

                String sqlStatement;
                if (caseInsensitive) {
                    sqlStatement = "SELECT type, data " +
                            "FROM variables " +
                            "WHERE LOWER(name) LIKE LOWER(?)";
                } else {
                    sqlStatement = "SELECT type, data FROM variables WHERE name LIKE ?";
                }

                PreparedStatement getStatement = tempConnection.prepareStatement(sqlStatement);

                String finalisedQuery = "%" + listName + "::%";

                getStatement.setString(1, finalisedQuery);

                ResultSet result = getStatement.executeQuery();

                int querySize = 0;

                while (result.next()) {
                    querySize++;
                }

                int valueArrayIndex = 0;

                for (int i = 0; i < values.length; i++) {

                    int storedListIndex = querySize + (i + 1);

                    PreparedStatement setStatement = tempConnection.prepareStatement("INSERT INTO variables (name, type, data) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE data=?, type=?");
                    String variableNameOut = listName + "::" + storedListIndex;

                    setStatement.setString(1, variableNameOut);
                    setStatement.setString(2, values[valueArrayIndex].type);
                    setStatement.setBytes(3, values[valueArrayIndex].data);

                    setStatement.setBytes(4, values[valueArrayIndex].data);
                    setStatement.setString(5, values[valueArrayIndex].type);

                    setStatement.executeUpdate();

                    valueArrayIndex++;

                }

            } else {

                Value value = values[0];

                String sqlStatement;
                if (caseInsensitive) {
                    sqlStatement = "SELECT name, data, type " +
                            "FROM variables " +
                            "WHERE LOWER(name) = LOWER(?)";
                } else {
                    sqlStatement = "SELECT data, type FROM variables WHERE name=?";
                }

                PreparedStatement getStatement = tempConnection.prepareStatement(sqlStatement);

                getStatement.setString(1, nameToRetrieve);

                ResultSet result = getStatement.executeQuery();

                if (result.next()) {

                    byte[] bytesOut = UniversalVariableUtils.add(result.getBytes("data"), value, result.getString("type"));

                    PreparedStatement setStatement = tempConnection.prepareStatement("INSERT INTO variables (name, type, data) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE data=?, type=?");

                    setStatement.setString(1, nameToRetrieve);
                    setStatement.setString(2, value.type);
                    setStatement.setBytes(3, bytesOut);

                    setStatement.setBytes(4, bytesOut);
                    setStatement.setString(5, value.type);

                    setStatement.executeUpdate();

                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
            getMainServer().getImpl().error("Error adding variable to MySQL Database!");
        }

    }

    @Override
    public void deleteVariable(@NotNull String name) {

        try (java.sql.Connection tempConnection = dataSource.getConnection()) {

            PreparedStatement preparedStatement;
            String nameToRetrieve;
            if (caseInsensitive) {
                nameToRetrieve = name.toLowerCase();
            } else {
                nameToRetrieve = name;
            }

            if (name.endsWith("::*")) {

                String sqlStatement;
                if (caseInsensitive) {
                    sqlStatement = "DELETE " +
                            "FROM variables " +
                            "WHERE LOWER(name) LIKE LOWER(?)";
                } else {
                    sqlStatement = "DELETE FROM variables WHERE name LIKE ?";
                }

                preparedStatement = tempConnection.prepareStatement(sqlStatement);

                String listName = nameToRetrieve.split("::\\*")[0];
                String finalisedQuery = "%" + listName + "::%";

                preparedStatement.setString(1, finalisedQuery);

            } else {
                preparedStatement = tempConnection.prepareStatement("DELETE FROM variables WHERE LOWER(name) = LOWER(?)");
                preparedStatement.setString(1, name);

            }
            preparedStatement.executeUpdate();

            //getMainServer().getImpl().logDebug("Deleted variable " + name);


        } catch (SQLException e) {

            e.printStackTrace();
            getMainServer().getImpl().error("Error deleting value from MySQL database!");

        }
    }

    @Override
    public void removeFromVariable(@NotNull String name, Value[] values) {

        try (java.sql.Connection tempConnection = dataSource.getConnection()) {

            String nameToRetrieve;
            if (caseInsensitive) {
                nameToRetrieve = name.toLowerCase();
            } else {
                nameToRetrieve = name;
            }

            if (name.endsWith("::*")) {

                String listName = nameToRetrieve.split("::\\*")[0];

                String sqlStatement;
                if (caseInsensitive) {
                    sqlStatement = "SELECT name, type, data " +
                            "FROM variables " +
                            "WHERE LOWER(name) LIKE LOWER(?)";
                } else {
                    sqlStatement = "SELECT name, type, data FROM variables WHERE name LIKE ?";
                }

                PreparedStatement getStatement = tempConnection.prepareStatement(sqlStatement);

                String finalisedQuery = "%" + listName + "::%";

                getStatement.setString(1, finalisedQuery);

                ResultSet result = getStatement.executeQuery();

                while (result.next()) {

                    for (Value value : values) {

                        if (Arrays.equals(value.data, result.getBytes("data"))) {

                            PreparedStatement deleteStatement = tempConnection.prepareStatement("DELETE FROM variables WHERE name=?");
                            deleteStatement.setString(1, result.getString("name"));

                            deleteStatement.executeUpdate();

                        }

                    }

                }


            } else {

                Value value = values[0];

                String sqlStatement;
                if (caseInsensitive) {
                    sqlStatement = "SELECT data, type " +
                            "FROM variables " +
                            "WHERE LOWER(name) = LOWER(?)";
                } else {
                    sqlStatement = "SELECT data, type FROM variables WHERE name=?";
                }

                PreparedStatement getStatement = tempConnection.prepareStatement(sqlStatement);

                getStatement.setString(1, name);
                ResultSet result = getStatement.executeQuery();

                if (result.next()) {

                    byte[] bytesOut = UniversalVariableUtils.subtract(result.getBytes("data"), value, result.getString("type"));

                    PreparedStatement setStatement = tempConnection.prepareStatement("INSERT INTO variables (name, type, data) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE data=?, type=?");

                    setStatement.setString(1, nameToRetrieve);
                    setStatement.setString(2, value.type);
                    setStatement.setBytes(3, bytesOut);

                    setStatement.setBytes(4, bytesOut);
                    setStatement.setString(5, value.type);

                    setStatement.executeUpdate();

                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
            getMainServer().getImpl().error("Error removing variable to MySQL Database!");
        }
    }

    @Override
    public void shutdown() {

        getMainServer().getImpl().logDebug("Shutting down database connection...");
        dataSource.close();

    }

}