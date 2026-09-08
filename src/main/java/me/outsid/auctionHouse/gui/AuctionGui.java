package me.outsid.auctionHouse.gui;

import me.outsid.auctionHouse.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class AuctionGui implements InventoryHolder {

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private final Inventory inventory;

    public AuctionGui() {
        Component title = MM.deserialize("<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>ᴀᴜᴄᴛɪᴏɴ ʜᴏᴜѕᴇ</gradient></bold>");
        this.inventory = Bukkit.createInventory(this, 54, title);

        // Remplissage obligatoire à l'instanciation
        fillGUI();
    }

    public static void open(Player player) {
        AuctionGui gui = new AuctionGui();
        player.openInventory(gui.getInventory());
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    private void fillGUI() {
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
        inventory.setItem(45, myItemsButton);

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
        inventory.setItem(46, sortingButton);

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
        inventory.setItem(48, previousPageButton);

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
        inventory.setItem(49, refreshButton);

        // Page suivante
        ItemStack nextPageButton = new ItemBuilder(Material.ARROW)
                .name("<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>ɴᴇxᴛ ᴘᴀɢᴇ</gradient></bold>")
                .lore(
                        "",
                        "<white>Click here to go to",
                        "<white>the next page!",
                        "",
                        "<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>Click</gradient></bold><white> » <color:#B1F5F8>Next Page</color>"
                )
                .build();
        inventory.setItem(50, nextPageButton);

        // Historique
        ItemStack historyButton = new ItemBuilder(Material.WRITABLE_BOOK)
                .name("<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>ʜɪѕᴛᴏʀʏ</gradient></bold>")
                .lore(
                        "<white>Click here to view your",
                        "<white>auction house history!"
                )
                .build();
        inventory.setItem(53, historyButton);
    }
}