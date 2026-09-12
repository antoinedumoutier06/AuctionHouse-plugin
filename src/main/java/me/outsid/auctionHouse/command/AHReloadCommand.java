package me.outsid.auctionHouse.command;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class AHReloadCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final JavaPlugin plugin;

    public AHReloadCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("auctionhouse.admin")) {
            sender.sendMessage(MM.deserialize("You do not have permission to execute this command."));
            return true;
        }

        plugin.reloadConfig();
        sender.sendMessage(MM.deserialize("AuctionHouse configuration successfully reloaded!"));
        return true;
    }
}