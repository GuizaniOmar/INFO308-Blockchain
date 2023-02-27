import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static DatabaseManager instance = null;
    private Connection connection = null;

    private DatabaseManager() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:database.db");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    public void insert(String table, String[] columns, String[] values) {
        StringBuilder columnBuilder = new StringBuilder();
        StringBuilder valueBuilder = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            columnBuilder.append(columns[i]);
            valueBuilder.append("?");
            if (i < columns.length - 1) {
                columnBuilder.append(",");
                valueBuilder.append(",");
            }
        }
        String sql = "INSERT INTO " + table + "(" + columnBuilder.toString() + ") VALUES(" + valueBuilder.toString() + ")";
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < values.length; i++) {
                statement.setString(i + 1, values[i]);
            }
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(String table, String[] columns, String[] values, String where) {
        StringBuilder setBuilder = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            setBuilder.append(columns[i] + "=?");
            if (i < columns.length - 1) {
                setBuilder.append(",");
            }
        }
        String sql = "UPDATE " + table + " SET " + setBuilder.toString() + " WHERE " + where;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            for (int i = 0; i < values.length; i++) {
                statement.setString(i + 1, values[i]);
            }
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(String table, String where) {
        String sql = "DELETE FROM " + table + " WHERE " + where;
        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet select(String table, String[] columns, String where) {
        StringBuilder columnBuilder = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            columnBuilder.append(columns[i]);
            if (i < columns.length - 1) {
                columnBuilder.append(",");
            }
        }
        String sql = "SELECT " + columnBuilder.toString() + " FROM " + table + " WHERE " + where;
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void createTable(String table, String[] columns, String[] types) {
        if (columns.length != types.length) {
            throw new IllegalArgumentException("Columns and types arrays must have the same length");
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            builder.append(columns[i] + " " + types[i]);
            if (i < columns.length - 1) {
                builder.append(",");
            }
        }
        String sql = "CREATE TABLE IF NOT EXISTS " + table + "(" + builder.toString() + ")";
        try {
            Statement statement = connection.createStatement();
            statement.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}