package me.outsid.auctionHouse;

import org.bukkit.plugin.java.JavaPlugin;

public final class AuctionHouse extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("AuctionHouse activé !");


        if (getCommand("auction") != null) {
            getCommand("auction").setExecutor(new AuctionCommand());
        }
    }

    @Override
    public void onDisable() {
        getLogger().info("AuctionHouse désactivé !");
    }
}
