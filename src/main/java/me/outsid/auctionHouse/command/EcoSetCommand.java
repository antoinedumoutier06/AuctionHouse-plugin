package me.outsid.auctionHouse.command;

import me.outsid.auctionHouse.economy.EconomyManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EcoSetCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final EconomyManager economyManager;

    public EcoSetCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage(MM.deserialize("This command can only be executed from the console!"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: /" + label + "  ");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Player not found or offline.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount < 0) {
                sender.sendMessage("Amount cannot be negative.");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("Please enter a valid amount.");
            return true;
        }

        economyManager.setBalance(target.getUniqueId(), amount);
        sender.sendMessage("Set " + target.getName() + "'s balance to $" + amount + ".");

        target.sendMessage(MM.deserialize("Your balance has been set to $" + amount + " by administration!"));

        return true;
    }
}
