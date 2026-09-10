package me.outsid.auctionHouse.command;

import me.outsid.auctionHouse.economy.EconomyManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PayCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final EconomyManager economyManager;

    public PayCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(MM.deserialize("<red>Only players can execute this command."));
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(MM.deserialize("<red>Usage: /pay <player> <amount>"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            player.sendMessage(MM.deserialize("<red>Player not found or offline."));
            return true;
        }

        if (target.getUniqueId().equals(player.getUniqueId())) {
            player.sendMessage(MM.deserialize("<red>You cannot send money to yourself!"));
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount <= 0) {
                player.sendMessage(MM.deserialize("<red>Amount must be greater than 0."));
                return true;
            }
        } catch (NumberFormatException e) {
            player.sendMessage(MM.deserialize("<red>Invalid amount specified."));
            return true;
        }

        if (!economyManager.has(player.getUniqueId(), amount)) {
            player.sendMessage(MM.deserialize("<red>You do not have enough money ($" + amount + " required)."));
            return true;
        }

        economyManager.withdraw(player.getUniqueId(), amount);
        economyManager.deposit(target.getUniqueId(), amount);

        player.sendMessage(MM.deserialize("<green>You sent <yellow>$" + amount + "</yellow> to <color:#B1F5F8>" + target.getName() + "</color>!"));
        target.sendMessage(MM.deserialize("<green>You received <yellow>$" + amount + "</yellow> from <color:#B1F5F8>" + player.getName() + "</color>!"));

        return true;
    }
}