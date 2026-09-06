package me.outsid.auctionHouse;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class AuctionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // Cast direct du sender en Player
        Player player = (Player) sender;

        // Création de l'inventaire HDV de 54 slots (6 lignes)
        Inventory hdv = Bukkit.createInventory(null, 54, ChatColor.DARK_BLUE + "Auction House");

        // Ouverture immédiate au joueur
        player.openInventory(hdv);

        return true;
    }
}