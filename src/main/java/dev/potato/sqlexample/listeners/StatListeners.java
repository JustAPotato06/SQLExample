package dev.potato.sqlexample.listeners;

import dev.potato.sqlexample.SQLExample;
import dev.potato.sqlexample.database.Database;
import dev.potato.sqlexample.models.PlayerStats;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.sql.SQLException;
import java.util.Date;

public class StatListeners implements Listener {
    private final SQLExample plugin = SQLExample.getPlugin();
    private final Database database = plugin.getDatabase();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();

        try {
            PlayerStats stats = getStatsFromDatabase(player);
            stats.setBlocksBroken(stats.getBlocksBroken() + 1);
            stats.setBalance(stats.getBalance() + 0.5);
            database.updatePlayerStats(stats);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player player = e.getEntity();
        Player killer = e.getEntity().getKiller();

        try {
            PlayerStats playerStats = getStatsFromDatabase(player);
            playerStats.setDeaths(playerStats.getDeaths() + 1);
            playerStats.setBalance(playerStats.getBalance() - 1);
            database.updatePlayerStats(playerStats);

            if (killer == null) return;

            PlayerStats killerStats = getStatsFromDatabase(killer);
            killerStats.setKills(killerStats.getKills() + 1);
            killerStats.setBalance(killerStats.getBalance() + 1);
            database.updatePlayerStats(killerStats);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        try {
            PlayerStats playerStats = getStatsFromDatabase(player);
            playerStats.setLastLogin(new Date());
            database.updatePlayerStats(playerStats);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        try {
            PlayerStats playerStats = getStatsFromDatabase(player);
            playerStats.setLastLogout(new Date());
            database.updatePlayerStats(playerStats);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private PlayerStats getStatsFromDatabase(Player player) throws SQLException {
        PlayerStats stats = database.findPlayerStats(player.getUniqueId().toString());

        if (stats == null) {
            stats = new PlayerStats(player.getUniqueId().toString(), 0, 0, 0, 0, new Date(), new Date());
            database.createPlayerStats(stats);
        }

        return stats;
    }
}