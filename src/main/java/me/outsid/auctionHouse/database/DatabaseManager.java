package me.outsid.auctionHouse.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

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
        String file = plugin.getConfig().getString("storage.file", "database.db");
        String path = plugin.getDataFolder().getAbsolutePath() + "/" + file;

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl("jdbc:sqlite:" + path);
        hikariConfig.setMaximumPoolSize(1);

        this.dataSource = new HikariDataSource(hikariConfig);

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute("PRAGMA journal_mode=WAL;");
        } catch (SQLException e) {
            plugin.getLogger().warning("Impossible d'activer le mode WAL : " + e.getMessage());
        }

        createTables();
    }

    private void createTables() {
        String economyTable = """
            CREATE TABLE IF NOT EXISTS economy (
                uuid VARCHAR(64) PRIMARY KEY,
                balance DOUBLE NOT NULL DEFAULT 0
            );
        """;

        String auctionsTable = """
            CREATE TABLE IF NOT EXISTS auctions (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                seller_uuid VARCHAR(64) NOT NULL,
                seller_name VARCHAR(32) NOT NULL,
                item_data MEDIUMTEXT NOT NULL,
                price DOUBLE NOT NULL,
                listed_at BIGINT NOT NULL,
                expires_at BIGINT NOT NULL,
                status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE'
            );
        """;

        String claimsTable = """
            CREATE TABLE IF NOT EXISTS auction_claims (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                owner_uuid VARCHAR(64) NOT NULL,
                type VARCHAR(32) NOT NULL,
                item_data MEDIUMTEXT,
                amount DOUBLE,
                created_at BIGINT NOT NULL,
                claimed BOOLEAN NOT NULL DEFAULT FALSE
            );
        """;

        String historyTable = """
            CREATE TABLE IF NOT EXISTS auction_history (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                seller_uuid VARCHAR(64) NOT NULL,
                buyer_uuid VARCHAR(64),
                item_data MEDIUMTEXT NOT NULL,
                price DOUBLE NOT NULL,
                action VARCHAR(32) NOT NULL,
                date BIGINT NOT NULL
            );
        """;

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(economyTable);
            stmt.execute(auctionsTable);
            stmt.execute(claimsTable);
            stmt.execute(historyTable);
        } catch (SQLException e) {
            plugin.getLogger().severe("Erreur lors de la création des tables : " + e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null) dataSource.close();
    }
}
