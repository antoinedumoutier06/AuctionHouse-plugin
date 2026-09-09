package me.outsid.auctionHouse.manager;

import me.outsid.auctionHouse.database.DatabaseManager;
import me.outsid.auctionHouse.model.AuctionItem;
import me.outsid.auctionHouse.model.ClaimItem;
import me.outsid.auctionHouse.util.ItemSerializer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AuctionManager {

    private final JavaPlugin plugin;
    private final DatabaseManager db;

    public AuctionManager(JavaPlugin plugin, DatabaseManager db) {
        this.plugin = plugin;
        this.db = db;
    }

    public CompletableFuture<Boolean> createAuction(Player player, ItemStack item, double price, long durationMillis) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = """
                INSERT INTO auctions (seller_uuid, seller_name, item_data, price, listed_at, expires_at, status)
                VALUES (?, ?, ?, ?, ?, ?, 'ACTIVE');
            """;

            long now = System.currentTimeMillis();
            long expiresAt = now + durationMillis;
            String itemData = ItemSerializer.toBase64(item);

            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, player.getUniqueId().toString());
                ps.setString(2, player.getName());
                ps.setString(3, itemData);
                ps.setDouble(4, price);
                ps.setLong(5, now);
                ps.setLong(6, expiresAt);
                ps.executeUpdate();
                return true;
            } catch (Exception e) {
                plugin.getLogger().severe("Error while creating auction: " + e.getMessage());
                return false;
            }
        });
    }

    public CompletableFuture<List<AuctionItem>> getActiveAuctions() {
        return CompletableFuture.supplyAsync(() -> {
            List<AuctionItem> list = new ArrayList<>();
            String sql = "SELECT * FROM auctions WHERE status = 'ACTIVE' AND expires_at > ? ORDER BY id DESC;";

            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, System.currentTimeMillis());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(mapResultSet(rs));
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Error while fetching active auctions: " + e.getMessage());
            }
            return list;
        });
    }

    public CompletableFuture<List<AuctionItem>> getActiveAuctionsByPlayer(UUID playerUuid) {
        return CompletableFuture.supplyAsync(() -> {
            List<AuctionItem> list = new ArrayList<>();
            String sql = "SELECT * FROM auctions WHERE seller_uuid = ? AND status = 'ACTIVE' AND expires_at > ? ORDER BY id DESC;";

            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, playerUuid.toString());
                ps.setLong(2, System.currentTimeMillis());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(mapResultSet(rs));
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Error while fetching player's active auctions: " + e.getMessage());
            }
            return list;
        });
    }

    public CompletableFuture<Integer> getActiveAuctionCount(UUID sellerUuid) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT COUNT(*) FROM auctions WHERE seller_uuid = ? AND status = 'ACTIVE' AND expires_at > ?;";
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, sellerUuid.toString());
                ps.setLong(2, System.currentTimeMillis());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Error while counting player's active auctions: " + e.getMessage());
            }
            return 0;
        });
    }

    /**
     * Atomically updates an auction's status (only if the current status is 'ACTIVE').
     * Returns true if a row was actually modified.
     */
    public CompletableFuture<Boolean> updateStatus(int auctionId, String newStatus) {
        return CompletableFuture.supplyAsync(() -> {
            // The "AND status = 'ACTIVE'" condition prevents buying an item that was already sold or cancelled
            String sql = "UPDATE auctions SET status = ? WHERE id = ? AND status = 'ACTIVE';";
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, newStatus);
                ps.setInt(2, auctionId);
                return ps.executeUpdate() > 0;
            } catch (Exception e) {
                plugin.getLogger().severe("Error updating status for auction #" + auctionId + ": " + e.getMessage());
                return false;
            }
        });
    }

    public CompletableFuture<List<ClaimItem>> getUnclaimedItems(UUID ownerUuid) {
        return CompletableFuture.supplyAsync(() -> {
            List<ClaimItem> list = new ArrayList<>();
            String sql = "SELECT * FROM auction_claims WHERE owner_uuid = ? AND claimed = 0 AND item_data IS NOT NULL ORDER BY id DESC;";

            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, ownerUuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        list.add(new ClaimItem(
                                rs.getInt("id"),
                                UUID.fromString(rs.getString("owner_uuid")),
                                rs.getString("type"),
                                ItemSerializer.fromBase64(rs.getString("item_data")),
                                rs.getLong("created_at")
                        ));
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().severe("Error while fetching unclaimed items: " + e.getMessage());
            }
            return list;
        });
    }

    public CompletableFuture<Boolean> addClaimItem(UUID ownerUuid, String type, ItemStack item) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "INSERT INTO auction_claims (owner_uuid, type, item_data, created_at, claimed) VALUES (?, ?, ?, ?, 0);";
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, ownerUuid.toString());
                ps.setString(2, type);
                ps.setString(3, ItemSerializer.toBase64(item));
                ps.setLong(4, System.currentTimeMillis());
                return ps.executeUpdate() > 0;
            } catch (Exception e) {
                plugin.getLogger().severe("Error while adding claim item: " + e.getMessage());
                return false;
            }
        });
    }

    public CompletableFuture<Boolean> markClaimed(int claimId) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "UPDATE auction_claims SET claimed = 1 WHERE id = ?;";
            try (Connection conn = db.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, claimId);
                return ps.executeUpdate() > 0;
            } catch (Exception e) {
                plugin.getLogger().severe("Error while validating claim #" + claimId + ": " + e.getMessage());
                return false;
            }
        });
    }

    private AuctionItem mapResultSet(ResultSet rs) throws Exception {
        return new AuctionItem(
                rs.getInt("id"),
                UUID.fromString(rs.getString("seller_uuid")),
                rs.getString("seller_name"),
                ItemSerializer.fromBase64(rs.getString("item_data")),
                rs.getDouble("price"),
                rs.getLong("listed_at"),
                rs.getLong("expires_at"),
                rs.getString("status")
        );
    }
}
