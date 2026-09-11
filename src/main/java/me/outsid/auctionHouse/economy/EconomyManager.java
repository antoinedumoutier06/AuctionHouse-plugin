package me.outsid.auctionHouse.economy;

import me.outsid.auctionHouse.database.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class EconomyManager {

    private final JavaPlugin plugin;
    private final DatabaseManager db;
    private final Map<UUID, Double> balanceCache = new ConcurrentHashMap<>();

    public EconomyManager(JavaPlugin plugin, DatabaseManager db) {
        this.plugin = plugin;
        this.db = db;
    }

    public void loadPlayer(UUID uuid) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            double balance = fetchBalance(uuid);
            balanceCache.put(uuid, balance);
        });
    }

    public void unloadPlayer(UUID uuid) {
        Double balance = balanceCache.remove(uuid);
        if (balance != null) {
            saveBalance(uuid, balance);
        }
    }

    public void saveAll() {
        balanceCache.forEach(this::saveBalance);
    }

    public double getBalance(UUID uuid) {
        return balanceCache.getOrDefault(uuid, 0.0);
    }

    public void setBalance(UUID uuid, double amount) {
        balanceCache.put(uuid, amount);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> saveBalance(uuid, amount));
    }

    public synchronized void deposit(UUID uuid, double amount) {
        double newBalance = getBalance(uuid) + amount;
        setBalance(uuid, newBalance);
    }

    public synchronized boolean withdraw(UUID uuid, double amount) {
        double current = getBalance(uuid);
        if (current < amount) return false;
        setBalance(uuid, current - amount);
        return true;
    }

    public boolean has(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }

    private double fetchBalance(UUID uuid) {
        String select = "SELECT balance FROM economy WHERE uuid = ?";
        String insertDefault = "INSERT INTO economy (uuid, balance) VALUES (?, ?)";

        try (Connection conn = db.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(select)) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble("balance");
                    }
                }
            }

            double startingBalance = plugin.getConfig().getDouble("economy.starting-balance", 0.0);
            try (PreparedStatement ps = conn.prepareStatement(insertDefault)) {
                ps.setString(1, uuid.toString());
                ps.setDouble(2, startingBalance);
                ps.executeUpdate();
            }
            return startingBalance;

        } catch (Exception e) {
            plugin.getLogger().severe("Erreur chargement solde " + uuid + " : " + e.getMessage());
            return 0.0;
        }
    }

    private void saveBalance(UUID uuid, double amount) {
        String upsert = """
            INSERT INTO economy (uuid, balance) VALUES (?, ?)
            ON CONFLICT(uuid) DO UPDATE SET balance = ?
        """;

        try (Connection conn = db.getConnection();
             PreparedStatement ps = conn.prepareStatement(upsert)) {
            ps.setString(1, uuid.toString());
            ps.setDouble(2, amount);
            ps.setDouble(3, amount);
            ps.executeUpdate();
        } catch (Exception e) {
            plugin.getLogger().severe("Erreur sauvegarde solde " + uuid + " : " + e.getMessage());
        }
    }

    public CompletableFuture<List<EconomyEntry>> getTopBalances(int limit) {
        return CompletableFuture.supplyAsync(() -> {
            List<EconomyEntry> topList = new ArrayList<>();
            String sql = "SELECT uuid, balance FROM economy ORDER BY balance DESC LIMIT ?;";
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, limit);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        UUID uuid = UUID.fromString(rs.getString("uuid"));
                        double balance = rs.getDouble("balance");
                        topList.add(new EconomyEntry(uuid, balance));
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Error fetching top balances: " + e.getMessage());
            }
            return topList;
        });
    }

    public record EconomyEntry(UUID uuid, double balance) {}
}
