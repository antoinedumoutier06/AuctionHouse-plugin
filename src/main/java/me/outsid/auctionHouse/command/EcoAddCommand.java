package me.outsid.auctionHouse.command;

import me.outsid.auctionHouse.economy.EconomyManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EcoAddCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final EconomyManager economyManager;

    public EcoAddCommand(EconomyManager economyManager) {
        this.economyManager = economyManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            sender.sendMessage(MM.deserialize("<red>Cette commande ne peut être exécutée que depuis la console !"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage("Usage: /" + label + " <player> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage("Joueur introuvable ou hors ligne.");
            return true;
        }

        double amount;
        try {
            amount = Double.parseDouble(args[1]);
            if (amount <= 0) {
                sender.sendMessage("Le montant doit être supérieur à 0.");
                return true;
            }
        } catch (NumberFormatException e) {
            sender.sendMessage("Veuillez entrer un montant valide.");
            return true;
        }

        economyManager.deposit(target.getUniqueId(), amount);
        sender.sendMessage("Ajouté $" + amount + " au compte de " + target.getName() + ".");

        target.sendMessage(MM.deserialize("<green>Vous avez reçu <yellow>$" + amount + "</yellow> de la part de l'administration !"));

        return true;
    }
}
