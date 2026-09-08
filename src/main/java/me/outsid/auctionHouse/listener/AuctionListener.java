package me.outsid.auctionHouse.listener;

import me.outsid.auctionHouse.gui.AuctionGui;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;

public class AuctionListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Vérifie si l'inventaire du HAUT est bien un AuctionGui
        if (event.getInventory().getHolder() instanceof AuctionGui) {

            // ANNULE LE CLIC SYSTÉMATIQUEMENT (empêche vol, shift-click, swap)
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getHolder() instanceof AuctionGui) {
            event.setCancelled(true);
        }
    }
}