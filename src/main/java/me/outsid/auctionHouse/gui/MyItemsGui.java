package me.outsid.auctionHouse.gui;

import me.outsid.auctionHouse.manager.AuctionManager;
import me.outsid.auctionHouse.model.AuctionItem;
import me.outsid.auctionHouse.model.ClaimItem;
import me.outsid.auctionHouse.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyItemsGui implements InventoryHolder {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final JavaPlugin plugin;
    private final AuctionManager auctionManager;
    private final Inventory inventory;

    private final Map<Integer, AuctionItem> activeAuctionSlots = new HashMap<>();
    private final Map<Integer, ClaimItem> claimItemSlots = new HashMap<>();

    public MyItemsGui(JavaPlugin plugin, AuctionManager auctionManager) {
        this.plugin = plugin;
        this.auctionManager = auctionManager;
        Component title = MM.deserialize("<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>ᴍʏ ɪᴛᴇᴍѕ</gradient></bold>");
        this.inventory = Bukkit.createInventory(this, 54, title);
    }

    public void open(Player player) {
        activeAuctionSlots.clear();
        claimItemSlots.clear();

        auctionManager.getActiveAuctionsByPlayer(player.getUniqueId()).thenAccept(activeAuctions -> {
            auctionManager.getUnclaimedItems(player.getUniqueId()).thenAccept(claimItems -> {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    inventory.clear();

                    ItemStack separator = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).name(" ").build();
                    for (int i = 18; i < 27; i++) {
                        inventory.setItem(i, separator);
                    }

                    int slot = 0;
                    for (AuctionItem auction : activeAuctions) {
                        if (slot >= 18) break;
                        inventory.setItem(slot, buildActiveAuctionDisplay(auction));
                        activeAuctionSlots.put(slot, auction);
                        slot++;
                    }

                    slot = 27;
                    for (ClaimItem claim : claimItems) {
                        if (slot >= 45) break;
                        inventory.setItem(slot, buildClaimDisplay(claim));
                        claimItemSlots.put(slot, claim);
                        slot++;
                    }

                    fillControls();
                    player.openInventory(inventory);
                });
            });
        });
    }

    private ItemStack buildActiveAuctionDisplay(AuctionItem auction) {
        ItemStack item = auction.getItem().clone();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        List<Component> lore = meta.hasLore() && meta.lore() != null ? new ArrayList<>(meta.lore()) : new ArrayList<>();
        lore.add(Component.empty());
        lore.add(MM.deserialize("<gray>Price: <green>$" + auction.getPrice() + "</green>"));
        lore.add(MM.deserialize("<gray>Expires in: <yellow>" + formatRemainingTime(auction.getExpiresAt()) + "</yellow>"));
        lore.add(Component.empty());
        lore.add(MM.deserialize("<red> Click » Cancel item </red>"));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    private ItemStack buildClaimDisplay(ClaimItem claim) {
        ItemStack item = claim.getItem().clone();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        List<Component> lore = meta.hasLore() && meta.lore() != null ? new ArrayList<>(meta.lore()) : new ArrayList<>();
        lore.add(Component.empty());
        lore.add(MM.deserialize("<gray>Status: <yellow>" + claim.getType() + "</yellow>"));
        lore.add(MM.deserialize("<green>Click » retrieve from your inventory</green>"));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }
    private String formatRemainingTime(long expiresAt) {
        long millis = expiresAt - System.currentTimeMillis();
        if (millis <= 0) return "<red>Expired</red>";

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + "d " + (hours % 24) + "h";
        if (hours > 0) return hours + "h " + (minutes % 60) + "m";
        if (minutes > 0) return minutes + "m " + (seconds % 60) + "s";
        return (seconds % 60) + "s";
    }

    private void fillControls() {
        ItemStack glass = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, glass);
        }

        ItemStack backButton = new ItemBuilder(Material.ARROW)
                .name("<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>ʙᴀᴄᴋ</gradient></bold>")
                .lore( "<white>Return to the main menu")
                .build();
        inventory.setItem(48, backButton);
    }

    public AuctionItem getActiveAuctionAt(int slot) { return activeAuctionSlots.get(slot); }
    public ClaimItem getClaimItemAt(int slot) { return claimItemSlots.get(slot); }

    @Override
    public Inventory getInventory() { return inventory; }
}