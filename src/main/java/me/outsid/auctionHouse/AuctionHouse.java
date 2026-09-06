package me.outsid.auctionHouse;

import org.bukkit.plugin.java.JavaPlugin;

public final class AuctionHouse extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("AuctionHouse activé !");
    }

    @Override
    public void onDisable() {
        getLogger().info("AuctionHouse désactivé !");
    }
}