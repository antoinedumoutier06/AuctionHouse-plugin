package me.outsid.auctionHouse.listener;

import me.outsid.auctionHouse.economy.EconomyManager;
import me.outsid.auctionHouse.gui.AuctionGui;
import me.outsid.auctionHouse.gui.MyItemsGui;
import me.outsid.auctionHouse.manager.AuctionManager;
import me.outsid.auctionHouse.model.AuctionItem;
import me.outsid.auctionHouse.model.ClaimItem;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class AuctionListener implements Listener {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final JavaPlugin plugin;
    private final AuctionManager auctionManager;
    private final EconomyManager economyManager;

    public AuctionListener(JavaPlugin plugin, AuctionManager auctionManager, EconomyManager economyManager) {
        this.plugin = plugin;
        this.auctionManager = auctionManager;
        this.economyManager = economyManager;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        if (event.getRawSlot() < 0 || event.getRawSlot() >= event.getInventory().getSize()) return;

        if (event.getInventory().getHolder() instanceof AuctionGui gui) {
            event.setCancelled(true);
            int slot = event.getSlot();

            if (slot == 45) {
                new MyItemsGui(plugin, auctionManager).open(player);
                return;
            }

            if (slot == 46) {
                gui.setSortType(gui.getSortType().next());
                gui.open(player);
                return;
            }

            if (slot == 48 && gui.getPage() > 0) {
                gui.setPage(gui.getPage() - 1);
                gui.open(player);
                return;
            }

            if (slot == 49) {
                gui.open(player);
                player.sendMessage(MM.deserialize("<green>Page refreshed!"));
                return;
            }

            if (slot == 50) {
                gui.setPage(gui.getPage() + 1);
                gui.open(player);
                return;
            }

            if (slot < 45) {
                AuctionItem auction = gui.getAuctionAtSlot(slot);
                if (auction == null) return;

                if (auction.getSellerUuid().equals(player.getUniqueId())) {
                    player.sendMessage(MM.deserialize("<red>You cannot buy your own item!"));
                    return;
                }

                double price = auction.getPrice();
                if (!economyManager.has(player.getUniqueId(), price)) {
                    player.sendMessage(MM.deserialize("<red>You do not have enough money ($" + price + " required)."));
                    return;
                }

                auctionManager.updateStatus(auction.getId(), "SOLD").thenAccept(success -> {
                    if (!success) {
                        player.sendMessage(MM.deserialize("<red>This item is no longer available!"));
                        Bukkit.getScheduler().runTask(plugin, () -> gui.open(player));
                        return;
                    }

                    auctionManager.addClaimItem(player.getUniqueId(), "PURCHASED", auction.getItem()).thenAccept(claimed -> {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            economyManager.withdraw(player.getUniqueId(), price);
                            economyManager.deposit(auction.getSellerUuid(), price);

                            player.sendMessage(MM.deserialize("<green>Successfully purchased for <yellow>$" + price + "<green>! Claim your item in your <yellow>Ender Chest (/ah)</yellow>."));
                            gui.open(player);
                        });
                    });
                });
            }
            return;
        }

        if (event.getInventory().getHolder() instanceof MyItemsGui myItemsGui) {
            event.setCancelled(true);
            int slot = event.getSlot();

            if (slot == 48) {
                new AuctionGui(plugin, auctionManager).open(player);
                return;
            }

            AuctionItem activeAuction = myItemsGui.getActiveAuctionAt(slot);
            if (activeAuction != null) {
                auctionManager.updateStatus(activeAuction.getId(), "CANCELLED").thenAccept(success -> {
                    if (success) {
                        auctionManager.addClaimItem(player.getUniqueId(), "CANCELLED", activeAuction.getItem()).thenAccept(claimed -> {
                            Bukkit.getScheduler().runTask(plugin, () -> {
                                player.sendMessage(MM.deserialize("<yellow>Listing cancelled. The item has been moved to your claims."));
                                myItemsGui.open(player);
                            });
                        });
                    }
                });
                return;
            }

            ClaimItem claimItem = myItemsGui.getClaimItemAt(slot);
            if (claimItem != null) {
                auctionManager.markClaimed(claimItem.getId()).thenAccept(success -> {
                    if (success) {
                        Bukkit.getScheduler().runTask(plugin, () -> {
                            HashMap<Integer, ItemStack> leftover = player.getInventory().addItem(claimItem.getItem());
                            if (!leftover.isEmpty()) {
                                for (ItemStack drop : leftover.values()) {
                                    player.getWorld().dropItemNaturally(player.getLocation(), drop);
                                }
                            }
                            player.sendMessage(MM.deserialize("<green>Item collected to your inventory!"));
                            myItemsGui.open(player);
                        });
                    }
                });
            }
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof AuctionGui ||
                event.getInventory().getHolder() instanceof MyItemsGui) {
            event.setCancelled(true);
        }
    }
}