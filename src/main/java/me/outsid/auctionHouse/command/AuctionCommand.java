package me.outsid.auctionHouse.command;

import me.outsid.auctionHouse.gui.AuctionGui;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AuctionCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Seul un joueur peut exécuter cette commande.");
            return true;
        }

        Player player = (Player) sender;
        AuctionGui.open(player);

        return true;
    }
}