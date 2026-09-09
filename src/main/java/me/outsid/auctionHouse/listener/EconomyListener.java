package me.outsid.auctionHouse.listener;

import me.outsid.auctionHouse.economy.EconomyManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class EconomyListener implements Listener {

    private final EconomyManager economyManager;

    public EconomyListener(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        economyManager.loadPlayer(event.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        economyManager.unloadPlayer(event.getPlayer().getUniqueId());
    }
}