package me.outsid.auctionHouse.command;

import me.outsid.auctionHouse.economy.EconomyManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BaltopCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final JavaPlugin plugin;
    private final EconomyManager economyManager;

    public BaltopCommand(JavaPlugin plugin, EconomyManager economyManager) {
        this.plugin = plugin;
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        sender.sendMessage(MM.deserialize("<gray>Loading top balances..."));

        economyManager.getTopBalances(10).thenAccept(topList -> {
            Bukkit.getScheduler().runTask(plugin, () -> {
                if (topList.isEmpty()) {
                    sender.sendMessage(MM.deserialize("<red>No economy records found."));
                    return;
                }

                sender.sendMessage(MM.deserialize("<gradient:#6FFFFF:#81FFFA><b>=== Top Balances ===</b></gradient>"));
                int rank = 1;
                for (EconomyManager.EconomyEntry entry : topList) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(entry.uuid());
                    String name = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Unknown";
                    sender.sendMessage(MM.deserialize("<gray>#" + rank + ". <color:#B1F5F8>" + name + "</color> - <green>$" + entry.balance() + "</green>"));
                    rank++;
                }
            });
        });

        return true;
    }
}