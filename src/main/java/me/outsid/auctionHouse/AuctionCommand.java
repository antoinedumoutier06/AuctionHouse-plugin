package me.outsid.auctionHouse;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AuctionCommand implements CommandExecutor {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        Player player = (Player) sender;

        Component title = MM.deserialize("<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>ᴀᴜᴄᴛɪᴏɴ ʜᴏᴜѕᴇ</gradient></bold>");
        Inventory hdv = Bukkit.createInventory(null, 54, title);

        fillGUI(hdv);

        player.openInventory(hdv);

        return true;
    }

    private void fillGUI(Inventory hdv) {

        // Mes items
        ItemStack myItemsButton = new ItemBuilder(Material.ENDER_CHEST)
                .name("<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>ᴍʏ ɪᴛᴇᴍѕ</gradient></bold>")
                .lore(
                        "",
                        "<white> Click here to go",
                        "<white> to our items page!",
                        "",
                        "<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>Click</gradient></bold><white> » <color:#B1F5F8>My items Page</color>",
                        "<color:#B1F5F8>You are selling </color><white>0/5<color:#B1F5F8> items!</color>",
                        "",
                        "<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>Sell Items</gradient></bold><white> » <color:#B1F5F8>/ah sell</color>"
                )
                .build();
        hdv.setItem(45, myItemsButton);

// Tri
        ItemStack sortingButton = new ItemBuilder(Material.GLOWSTONE_DUST)
                .name("<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>ѕᴏʀᴛɪɴɢ</gradient></bold>")
                .lore(
                        "",
                        "<gray>Currently selected:",
                        "<green> • Newest to oldest",
                        "<white> • Oldest to newest",
                        "<white> • Cheapest to priciest",
                        "<white> • Priciest to cheapest",
                        "",
                        "<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>Click</gradient></bold><white> » <color:#B1F5F8>Change sorting</color>"
                )
                .build();
        hdv.setItem(46, sortingButton);

// Page précédente
        ItemStack previousPageButton = new ItemBuilder(Material.ARROW)
                .name("<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>ᴘʀᴇᴠɪᴏᴜѕ ᴘᴀɢᴇ</gradient></bold>")
                .lore(
                        "",
                        "<white>Click here to go back",
                        "<white>to the previous page!",
                        "",
                        "<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>Click</gradient></bold><white> » <color:#B1F5F8>Previous Page</color>"
                )
                .build();
        hdv.setItem(48, previousPageButton);

// Refresh
        ItemStack refreshButton = new ItemBuilder(Material.SUNFLOWER)
                .name("<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>ʀᴇꜰʀᴇѕʜ</gradient></bold>")
                .lore(
                        "",
                        "<white>Click here to refresh",
                        "<white>the auction house page!",
                        "",
                        "<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>Click</gradient></bold><white> » <color:#B1F5F8>Refresh Page</color>",
                        "<italic><dark_gray>Refreshes item list & prices</italic>"
                )
                .build();
        hdv.setItem(49, refreshButton);

// Page suivante
        ItemStack nextPageButton = new ItemBuilder(Material.ARROW) // Remplacé CHEST par ARROW pour la cohérence des pages
                .name("<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>ɴᴇxᴛ ᴘᴀɢᴇ</gradient></bold>")
                .lore(
                        "",
                        "<white>Click here to go to",
                        "<white>the next page!",
                        "",
                        "<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>Click</gradient></bold><white> » <color:#B1F5F8>Next Page</color>"
                )
                .build();
        hdv.setItem(50, nextPageButton);

// Historique
        ItemStack historyButton = new ItemBuilder(Material.WRITABLE_BOOK)
                .name("<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>ʜɪѕᴛᴏʀʏ</gradient></bold>")
                .lore(
                        "<white>Click here to view your",
                        "<white>auction house history!"
                )
                .build();
        hdv.setItem(53, historyButton);
    }
}
