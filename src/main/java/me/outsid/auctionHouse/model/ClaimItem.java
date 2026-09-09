package me.outsid.auctionHouse.model;

import org.bukkit.inventory.ItemStack;
import java.util.UUID;

public class ClaimItem {
    private final int id;
    private final UUID ownerUuid;
    private final String type; // "PURCHASED", "CANCELLED", "EXPIRED"
    private final ItemStack item;
    private final long createdAt;

    public ClaimItem(int id, UUID ownerUuid, String type, ItemStack item, long createdAt) {
        this.id = id;
        this.ownerUuid = ownerUuid;
        this.type = type;
        this.item = item;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public UUID getOwnerUuid() { return ownerUuid; }
    public String getType() { return type; }
    public ItemStack getItem() { return item; }
    public long getCreatedAt() { return createdAt; }
}