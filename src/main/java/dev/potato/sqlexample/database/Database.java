package dev.potato.sqlexample.database;

import dev.potato.sqlexample.SQLExample;
import dev.potato.sqlexample.models.PlayerStats;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;

import java.sql.*;

public class Database {
    private final SQLExample plugin = SQLExample.getPlugin();
    private final String HOST;
    private final String PORT;
    private final String USER;
    private final String PASSWORD;
    private final String DATABASE_NAME;
    private final String TYPE;
    private Connection connection;

    public Database(String HOST, String PORT, String USER, String PASSWORD, String DATABASE_NAME, String TYPE) {
        this.HOST = HOST;
        this.PORT = PORT;
        this.USER = USER;
        this.PASSWORD = PASSWORD;
        this.DATABASE_NAME = DATABASE_NAME;
        this.TYPE = TYPE;
    }

    public Connection getConnection() throws SQLException {
        if (connection != null) return connection;

        String url = "jdbc:" + TYPE + "://" + HOST + "/" + DATABASE_NAME;

        connection = DriverManager.getConnection(url, USER, PASSWORD);
        Bukkit.getConsoleSender().sendMessage(Component.text("[SQL Example] Database connected successfully!", NamedTextColor.GREEN));

        return connection;
    }

    public void initializeDatabase() throws SQLException {
        Statement statement = getConnection().createStatement();
        String sql = "CREATE TABLE IF NOT EXISTS player_stats(uuid varchar(36) primary key, deaths int, kills int, blocks_broken long, balance double, last_login DATE, last_logout DATE)";
        statement.execute(sql);
        statement.close();
        Bukkit.getConsoleSender().sendMessage(Component.text("[SQL Example] player_stats database table created successfully!", NamedTextColor.GREEN));
    }

    public PlayerStats findPlayerStats(String uuid) throws SQLException {
        String sql = "SELECT * FROM player_stats WHERE uuid = ?";
        PreparedStatement statement = getConnection().prepareStatement(sql);
        statement.setString(1, uuid);
        ResultSet resultSet = statement.executeQuery();

        if (resultSet.next()) {
            int deaths = resultSet.getInt("deaths");
            int kills = resultSet.getInt("kills");
            long blocksBroken = resultSet.getLong("blocks_broken");
            double balance = resultSet.getDouble("balance");
            Date lastLogin = resultSet.getDate("last_login");
            Date lastLogout = resultSet.getDate("last_logout");
            PlayerStats playerStats = new PlayerStats(uuid, deaths, kills, blocksBroken, balance, lastLogin, lastLogout);
            statement.close();
            return playerStats;
        }

        statement.close();
        return null;
    }

    public void createPlayerStats(PlayerStats stats) throws SQLException {
        String sql = "INSERT INTO player_stats(uuid, deaths, kills, blocks_broken, balance, last_login, last_logout) VALUES (?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = getConnection().prepareStatement(sql);
        statement.setString(1, stats.getUuid());
        statement.setInt(2, stats.getDeaths());
        statement.setInt(3, stats.getKills());
        statement.setLong(4, stats.getBlocksBroken());
        statement.setDouble(5, stats.getBalance());
        statement.setDate(6, new Date(stats.getLastLogin().getTime()));
        statement.setDate(7, new Date(stats.getLastLogout().getTime()));
        statement.executeUpdate();
        statement.close();
    }

    public void updatePlayerStats(PlayerStats stats) throws SQLException {
        String sql = "UPDATE player_stats SET deaths = ?, kills = ?, blocks_broken = ?, balance = ?, last_login = ?, last_logout = ? WHERE uuid = ?";
        PreparedStatement statement = getConnection().prepareStatement(sql);
        statement.setInt(1, stats.getDeaths());
        statement.setInt(2, stats.getKills());
        statement.setLong(3, stats.getBlocksBroken());
        statement.setDouble(4, stats.getBalance());
        statement.setDate(5, new Date(stats.getLastLogin().getTime()));
        statement.setDate(6, new Date(stats.getLastLogout().getTime()));
        statement.setString(7, stats.getUuid());
        statement.executeUpdate();
        statement.close();
    }

    public void deletePlayerStats(String uuid) throws SQLException {
        String sql = "DELETE FROM player_stats WHERE uuid = ?";
        PreparedStatement statement = getConnection().prepareStatement(sql);
        statement.setString(1, uuid);
        statement.executeUpdate();
        statement.close();
    }

    public void closeConnection() {
        try {
            if (connection == null) return;
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}