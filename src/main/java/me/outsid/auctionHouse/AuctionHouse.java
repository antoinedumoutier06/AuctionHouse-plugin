package me.outsid.auctionHouse;

import me.outsid.auctionHouse.command.AuctionCommand;
import me.outsid.auctionHouse.listener.AuctionListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuctionHouse extends JavaPlugin {

    @Override
    public void onEnable() {
        getLogger().info("AuctionHouse activé !");


        if (getCommand("auction") != null) {
            getCommand("auction").setExecutor(new AuctionCommand());
        }
        getServer().getPluginManager().registerEvents(new AuctionListener(), this);
    }

    @Override
    public void onDisable() {
        getLogger().info("AuctionHouse désactivé !");
    }
}
