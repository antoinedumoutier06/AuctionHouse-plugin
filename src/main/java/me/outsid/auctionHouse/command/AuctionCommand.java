package me.outsid.auctionHouse.command;

import me.outsid.auctionHouse.gui.AuctionGui;
import me.outsid.auctionHouse.manager.AuctionManager;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class AuctionCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private final JavaPlugin plugin;
    private final AuctionManager auctionManager;

    public AuctionCommand(JavaPlugin plugin, AuctionManager auctionManager) {
        this.plugin = plugin;
        this.auctionManager = auctionManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can execute this command.");
            return true;
        }

        if (args.length == 0) {
            AuctionGui gui = new AuctionGui(plugin, auctionManager);
            gui.open(player);
            return true;
        }

        if (args[0].equalsIgnoreCase("sell")) {
            if (args.length < 2) {
                player.sendMessage(MM.deserialize("<red>Usage: /" + label + " sell <price>"));
                return true;
            }

            double price;
            try {
                price = Double.parseDouble(args[1]);
                if (price <= 0) {
                    player.sendMessage(MM.deserialize("<red>Price must be greater than 0!"));
                    return true;
                }
            } catch (NumberFormatException e) {
                player.sendMessage(MM.deserialize("<red>Please enter a valid number for the price."));
                return true;
            }

            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.getType() == Material.AIR) {
                player.sendMessage(MM.deserialize("<red>You must hold an item in your hand!"));
                return true;
            }

            auctionManager.getActiveAuctionCount(player.getUniqueId()).thenAccept(count -> {
                if (count >= 5) {
                    player.sendMessage(MM.deserialize("<red>You have reached the maximum limit of 5 active listings!"));
                    return;
                }

                Bukkit.getScheduler().runTask(plugin, () -> {
                    ItemStack toSell = itemInHand.clone();
                    player.getInventory().setItemInMainHand(null);

                    long durationMillis = 48L * 60L * 60L * 1000L;

                    auctionManager.createAuction(player, toSell, price, durationMillis).thenAccept(success -> {
                        if (success) {
                            player.sendMessage(MM.deserialize("<green>Your item was successfully listed for <yellow>$" + price + "<green>!"));
                        } else {
                            Bukkit.getScheduler().runTask(plugin, () -> player.getInventory().addItem(toSell));
                            player.sendMessage(MM.deserialize("<red>An error occurred while listing your item."));
                        }
                    });
                });
            });

            return true;
        }

        player.sendMessage(MM.deserialize("<red>Unknown command. Usage: /" + label + " or /" + label + " sell <price>"));
        return true;
    }
}