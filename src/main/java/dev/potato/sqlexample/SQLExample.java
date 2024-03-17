package dev.potato.sqlexample;

import dev.potato.sqlexample.database.Database;
import dev.potato.sqlexample.listeners.StatListeners;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public final class SQLExample extends JavaPlugin {
    private static SQLExample plugin;
    private Database database;

    public static SQLExample getPlugin() {
        return plugin;
    }

    public Database getDatabase() {
        return database;
    }

    @Override
    public void onEnable() {
        // Initialization
        plugin = this;

        // MySQL
        initializeSQL();

        // Listeners
        registerListeners();
    }

    @Override
    public void onDisable() {
        // MySQL
        database.closeConnection();
    }

    private void initializeSQL() {
        try {
            database = new Database();
            database.initializeDatabase();
        } catch (SQLException e) {
            Bukkit.getConsoleSender().sendMessage(Component.text("[SQL Example] Unable to connect to the database and create tables!", NamedTextColor.RED));
            e.printStackTrace();
        }
    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new StatListeners(), this);
    }
}