package me.outsid.auctionHouse.command;

import me.outsid.auctionHouse.economy.EconomyManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MoneyCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final EconomyManager economyManager;

    public MoneyCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
                sender.sendMessage(MM.deserialize("<red>Player not found or offline."));
                return true;
            }

            double targetBalance = economyManager.getBalance(target.getUniqueId());
            sender.sendMessage(MM.deserialize("<gray>Balance of <color:#B1F5F8>" + target.getName() + "</color>: <green>$" + targetBalance + "</green>"));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(MM.deserialize("<red>Only players can check their own balance. Use /money <player>."));
            return true;
        }

        double balance = economyManager.getBalance(player.getUniqueId());
        player.sendMessage(MM.deserialize("<gray>Your balance: <green>$" + balance + "</green>"));

        return true;
    }
}
