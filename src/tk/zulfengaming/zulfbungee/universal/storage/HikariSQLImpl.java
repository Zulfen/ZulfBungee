package tk.zulfengaming.zulfbungee.universal.storage;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.NotNull;
import tk.zulfengaming.zulfbungee.universal.command.util.ChatColour;
import tk.zulfengaming.zulfbungee.universal.interfaces.StorageImpl;
import tk.zulfengaming.zulfbungee.universal.socket.MainServer;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.NetworkVariable;
import tk.zulfengaming.zulfbungee.universal.socket.objects.client.skript.Value;
import tk.zulfengaming.zulfbungee.universal.storage.util.ValueOperation;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

public abstract class HikariSQLImpl<P> extends StorageImpl<P> {

    private final HikariDataSource dataSource;

    public HikariSQLImpl(MainServer<P> mainServerIn) {
        super(mainServerIn);
        this.dataSource = initialiseDataSource();
    }

    protected abstract HikariDataSource initialiseDataSource();

    @Override
    public void setupDatabase() {

        try (java.sql.Connection tempConnection = dataSource.getConnection()) {

            getMainServer().getPluginInstance().logInfo(ChatColour.GREEN + "Storage successfully started!");

            String creationStatement = "CREATE TABLE IF NOT EXISTS variables " +
                    "(name VARCHAR(255) not NULL PRIMARY KEY, " +
                    " type VARCHAR(128) not NULL,  " +
                    " data VARBINARY(8000))";

            Statement statement = tempConnection.createStatement();
            statement.execute(creationStatement);

            getMainServer().getPluginInstance().logInfo(ChatColour.GREEN + "Done setting up the database!");


        } catch (SQLException e) {
            getMainServer().getPluginInstance().error("There was an error setting up/connecting to the database!");
            e.printStackTrace();
        }

    }

    @Override
    public Optional<NetworkVariable> getVariable(@NotNull String name) {

        try (java.sql.Connection tempConnection = dataSource.getConnection()) {

            if (name.endsWith("::*")) {

                String listName = name.split("::\\*")[0];

                PreparedStatement preparedStatement = tempConnection.prepareStatement("SELECT name, type, data FROM variables WHERE name LIKE ?");
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

                PreparedStatement preparedStatement = tempConnection.prepareStatement("SELECT data, type FROM variables WHERE name=? COLLATE SQL_Latin1_General_CP1_CI_AS");

                preparedStatement.setString(1, name);

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
            getMainServer().getPluginInstance().error("Error fetching data from MySQL database!");
        }

        return Optional.empty();

    }

    @Override
    public void setVariable(@NotNull NetworkVariable variable) {

        try (java.sql.Connection tempConnection = dataSource.getConnection()) {

            String variableNameIn = variable.getName();

            if (variableNameIn.endsWith("::*")) {
                Value[] variableValuesIn = variable.getValueArray();
                String variableNameInRoot = variableNameIn.split("::\\*")[0];

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

                    //getMainServer().getPluginInstance().logDebug("Stored variable in list " + variableNameIn);

                }

            } else {

                if (variable.getValueArray().length > 0) {

                    Value value = variable.getSingleValue();

                    PreparedStatement preparedStatement = tempConnection.prepareStatement("INSERT INTO variables (name, type, data) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE data=?, type=?");

                    preparedStatement.setString(1, variableNameIn);
                    preparedStatement.setString(2, value.type);
                    preparedStatement.setBytes(3, value.data);

                    preparedStatement.setBytes(4, value.data);
                    preparedStatement.setString(5, value.type);

                    preparedStatement.executeUpdate();

                    //getMainServer().getPluginInstance().logDebug("Stored variable " + variableNameIn);

                } else {
                    getMainServer().getPluginInstance().logDebug(String.format("%sVariable %s appears to be empty? Unable to save.", ChatColour.YELLOW, variableNameIn));
                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
            getMainServer().getPluginInstance().error("Error inserting value into MySQL database!");

        }
    }

    @Override
    public void addToVariable(@NotNull String name, Value[] values) {

        try (java.sql.Connection tempConnection = dataSource.getConnection()) {

            if (name.endsWith("::*")) {

                String listName = name.split("::\\*")[0];

                PreparedStatement getStatement = tempConnection.prepareStatement("SELECT name, type, data FROM variables WHERE name LIKE ?");

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

                PreparedStatement getStatement = tempConnection.prepareStatement("SELECT data, type FROM variables WHERE name=?");

                getStatement.setString(1, name);

                ResultSet result = getStatement.executeQuery();

                if (result.next()) {

                    byte[] bytesOut = ValueOperation.add(result.getBytes("data"), value, result.getString("type"));

                    PreparedStatement setStatement = tempConnection.prepareStatement("INSERT INTO variables (name, type, data) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE data=?, type=?");

                    setStatement.setString(1, name);
                    setStatement.setString(2, value.type);
                    setStatement.setBytes(3, bytesOut);

                    setStatement.setBytes(4, bytesOut);
                    setStatement.setString(5, value.type);

                    setStatement.executeUpdate();

                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
            getMainServer().getPluginInstance().error("Error adding variable to MySQL Database!");
        }

    }

    @Override
    public void deleteVariable(@NotNull String name) {

        try (java.sql.Connection tempConnection = dataSource.getConnection()) {

            PreparedStatement preparedStatement;

            if (name.endsWith("::*")) {

                preparedStatement = tempConnection.prepareStatement("DELETE FROM variables WHERE name LIKE ?");

                String listName = name.split("::\\*")[0];
                String finalisedQuery = "%" + listName + "::%";

                preparedStatement.setString(1, finalisedQuery);

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
    public void removeFromVariable(@NotNull String name, Value[] values) {

        try (java.sql.Connection tempConnection = dataSource.getConnection()) {

            if (name.endsWith("::*")) {

                String listName = name.split("::\\*")[0];

                PreparedStatement getStatement = tempConnection.prepareStatement("SELECT name, type, data FROM variables WHERE name LIKE ?");

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

                PreparedStatement getStatement = tempConnection.prepareStatement("SELECT data, type FROM variables WHERE name=?");

                getStatement.setString(1, name);
                ResultSet result = getStatement.executeQuery();

                if (result.next()) {

                    byte[] bytesOut = ValueOperation.subtract(result.getBytes("data"), value, result.getString("type"));

                    PreparedStatement setStatement = tempConnection.prepareStatement("INSERT INTO variables (name, type, data) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE data=?, type=?");

                    setStatement.setString(1, name);
                    setStatement.setString(2, value.type);
                    setStatement.setBytes(3, bytesOut);

                    setStatement.setBytes(4, bytesOut);
                    setStatement.setString(5, value.type);

                    setStatement.executeUpdate();

                }

            }

        } catch (SQLException e) {
            e.printStackTrace();
            getMainServer().getPluginInstance().error("Error removing variable to MySQL Database!");
        }
    }

    @Override
    public void shutdown() {

        getMainServer().getPluginInstance().logDebug("Shutting down database connection...");
        dataSource.close();

    }

}
