package me.outsid.auctionHouse.model;

import me.outsid.auctionHouse.util.ItemSerializer;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class AuctionItem {

    private final int id;
    private final UUID sellerUuid;
    private final String sellerName;
    private final ItemStack item;
    private final double price;
    private final long listedAt;
    private final long expiresAt;
    private String status;

    public AuctionItem(int id, UUID sellerUuid, String sellerName, ItemStack item, double price, long listedAt, long expiresAt, String status) {
        this.id = id;
        this.sellerUuid = sellerUuid;
        this.sellerName = sellerName;
        this.item = item;
        this.price = price;
        this.listedAt = listedAt;
        this.expiresAt = expiresAt;
        this.status = status;
    }

    public int getId() { return id; }
    public UUID getSellerUuid() { return sellerUuid; }
    public String getSellerName() { return sellerName; }
    public ItemStack getItem() { return item.clone(); }
    public double getPrice() { return price; }
    public long getListedAt() { return listedAt; }
    public long getExpiresAt() { return expiresAt; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public boolean isExpired() {
        return System.currentTimeMillis() > expiresAt;
    }
}