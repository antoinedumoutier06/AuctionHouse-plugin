package me.outsid.auctionHouse.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private final JavaPlugin plugin;
    private HikariDataSource dataSource;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void connect() {
        String fileName = plugin.getConfig().getString("storage.file", "database.db");
        File databaseFile = new File(plugin.getDataFolder(), fileName);

        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());

        config.setMaximumPoolSize(1);
        config.setConnectionTimeout(10000);

        config.addDataSourceProperty("journal_mode", "WAL");
        config.addDataSourceProperty("synchronous", "NORMAL");
        config.addDataSourceProperty("busy_timeout", "5000");

        this.dataSource = new HikariDataSource(config);

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA foreign_keys = ON;");
        } catch (SQLException e) {
            plugin.getLogger().warning("Impossible d'activer les foreign keys SQLite : " + e.getMessage());
        }

        createTables();
    }

    private void createTables() {
        String economyTable = """
            CREATE TABLE IF NOT EXISTS economy (
                uuid VARCHAR(36) PRIMARY KEY,
                balance REAL NOT NULL DEFAULT 0.0
            );
        """;

        String auctionsTable = """
            CREATE TABLE IF NOT EXISTS auctions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                seller_uuid VARCHAR(36) NOT NULL,
                seller_name VARCHAR(16) NOT NULL,
                item_data TEXT NOT NULL,
                price REAL NOT NULL,
                listed_at BIGINT NOT NULL,
                expires_at BIGINT NOT NULL,
                status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE'
            );
        """;

        String claimsTable = """
            CREATE TABLE IF NOT EXISTS auction_claims (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                owner_uuid VARCHAR(36) NOT NULL,
                type VARCHAR(16) NOT NULL,
                item_data TEXT,
                amount REAL,
                created_at BIGINT NOT NULL,
                claimed INTEGER NOT NULL DEFAULT 0
            );
        """;

        String historyTable = """
            CREATE TABLE IF NOT EXISTS auction_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                seller_uuid VARCHAR(36) NOT NULL,
                buyer_uuid VARCHAR(36),
                item_data TEXT NOT NULL,
                price REAL NOT NULL,
                action VARCHAR(16) NOT NULL,
                date BIGINT NOT NULL
            );
        """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(economyTable);
            stmt.execute(auctionsTable);
            stmt.execute(claimsTable);
            stmt.execute(historyTable);
        } catch (SQLException e) {
            plugin.getLogger().severe("Erreur lors de la création des tables SQLite : " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("La base de données n'est pas connectée.");
        }
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("Connexion à la base de données fermée proprement.");
        }
    }
}
