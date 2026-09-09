package me.outsid.auctionHouse;

import me.outsid.auctionHouse.command.AuctionCommand;
import me.outsid.auctionHouse.database.DatabaseManager;
import me.outsid.auctionHouse.economy.EconomyManager;
import me.outsid.auctionHouse.listener.AuctionListener;
import me.outsid.auctionHouse.listener.EconomyListener;
import me.outsid.auctionHouse.manager.AuctionManager;
import me.outsid.auctionHouse.command.EcoAddCommand;
import me.outsid.auctionHouse.command.MoneyCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class AuctionHouse extends JavaPlugin {

    private static AuctionHouse instance;
    private DatabaseManager databaseManager;
    private EconomyManager economyManager;
    private AuctionManager auctionManager;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        databaseManager = new DatabaseManager(this);
        databaseManager.connect();

        economyManager = new EconomyManager(this, databaseManager);
        auctionManager = new AuctionManager(this, databaseManager);

        getServer().getPluginManager().registerEvents(new AuctionListener(this, auctionManager, economyManager), this);
        getServer().getPluginManager().registerEvents(new EconomyListener(economyManager), this);

        var cmd = getCommand("auction");
        if (cmd != null) {
            cmd.setExecutor(new AuctionCommand(this, auctionManager));
        }

        getLogger().info("AuctionHouse activé avec succès !");
        if (getCommand("ecoadd") != null) {
            getCommand("ecoadd").setExecutor(new EcoAddCommand(this.economyManager));
        }
        if (getCommand("money") != null) {
            getCommand("money").setExecutor(new MoneyCommand(this.economyManager));
        }
    }

    @Override
    public void onDisable() {
        if (economyManager != null) {
            economyManager.saveAll();
        }

        if (databaseManager != null) {
            databaseManager.close();
        }

        getLogger().info("AuctionHouse désactivé !");
    }

    public static AuctionHouse getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public EconomyManager getEconomyManager() {
        return economyManager;
    }

    public AuctionManager getAuctionManager() {
        return auctionManager;
    }
}