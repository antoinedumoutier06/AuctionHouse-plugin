package me.outsid.auctionHouse.gui;

import me.outsid.auctionHouse.manager.AuctionManager;
import me.outsid.auctionHouse.model.AuctionItem;
import me.outsid.auctionHouse.util.ItemBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.util.*;

public class AuctionGui implements InventoryHolder {

    public enum SortType {
        NEWEST("Newest to oldest"),
        OLDEST("Oldest to newest"),
        PRICE_LOW("Cheapest to priciest"),
        PRICE_HIGH("Priciest to cheapest");

        private final String label;
        SortType(String label) { this.label = label; }
        public String getLabel() { return label; }
        public SortType next() {
            SortType[] values = values();
            return values[(this.ordinal() + 1) % values.length];
        }
    }

    private static final MiniMessage MM = MiniMessage.miniMessage();
    private static final DecimalFormat PRICE_FORMAT = new DecimalFormat("#,##0.00");

    private final JavaPlugin plugin;
    private final AuctionManager auctionManager;
    private final Inventory inventory;
    private final Map<Integer, AuctionItem> slotMap = new HashMap<>();

    private int page = 0;
    private SortType sortType = SortType.NEWEST;

    public AuctionGui(JavaPlugin plugin, AuctionManager auctionManager) {
        this.plugin = plugin;
        this.auctionManager = auctionManager;

        Component title = MM.deserialize("<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>ᴀᴜᴄᴛɪᴏɴ ʜᴏᴜѕᴇ</gradient></bold>");
        this.inventory = Bukkit.createInventory(this, 54, title);
    }

    public void open(Player player) {
        auctionManager.getActiveAuctions().thenAccept(items -> {
            auctionManager.getActiveAuctionCount(player.getUniqueId()).thenAccept(userItemCount -> {
                sortItems(items);

                Bukkit.getScheduler().runTask(plugin, () -> {
                    for (int i = 0; i < 45; i++) {
                        inventory.setItem(i, null);
                    }
                    slotMap.clear();

                    int startIndex = page * 45;
                    int endIndex = Math.min(startIndex + 45, items.size());

                    int slot = 0;
                    for (int i = startIndex; i < endIndex; i++) {
                        AuctionItem auction = items.get(i);
                        ItemStack displayItem = buildDisplayItem(auction);
                        inventory.setItem(slot, displayItem);
                        slotMap.put(slot, auction);
                        slot++;
                    }

                    fillGUIControls(items.size(), userItemCount);
                    player.openInventory(inventory);
                });
            });
        });
    }

    private void sortItems(List<AuctionItem> items) {
        switch (sortType) {
            case NEWEST -> items.sort((a, b) -> Long.compare(b.getListedAt(), a.getListedAt()));
            case OLDEST -> items.sort((a, b) -> Long.compare(a.getListedAt(), b.getListedAt()));
            case PRICE_LOW -> items.sort((a, b) -> Double.compare(a.getPrice(), b.getPrice()));
            case PRICE_HIGH -> items.sort((a, b) -> Double.compare(b.getPrice(), a.getPrice()));
        }
    }

    private void fillGUIControls(int totalItems, int userItemCount) {
        ItemStack glass = new ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).name(" ").build();
        for (int i = 45; i < 54; i++) {
            inventory.setItem(i, glass);
        }

        ItemStack myItemsButton = new ItemBuilder(Material.ENDER_CHEST)
                .name("<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>ᴍʏ ɪᴛᴇᴍѕ</gradient></bold>")
                .lore(
                        "",
                        "<white> Click here to go",
                        "<white> to our items page!",
                        "",
                        "<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>Click</gradient></bold><white> » <color:#B1F5F8>My items Page</color>",
                        "<color:#B1F5F8>You are selling </color><white>" + userItemCount + "/5<color:#B1F5F8> items!</color>",
                        "",
                        "<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>Sell Items</gradient></bold><white> » <color:#B1F5F8>/ah sell</color>"
                )
                .build();
        inventory.setItem(45, myItemsButton);

        ItemStack sortingButton = new ItemBuilder(Material.GLOWSTONE_DUST)
                .name("<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>ѕᴏʀᴛɪɴɢ</gradient></bold>")
                .lore(
                        "",
                        "<gray>Currently selected:",
                        (sortType == SortType.NEWEST ? "<green> • Newest to oldest" : "<white> • Newest to oldest"),
                        (sortType == SortType.OLDEST ? "<green> • Oldest to newest" : "<white> • Oldest to newest"),
                        (sortType == SortType.PRICE_LOW ? "<green> • Cheapest to priciest" : "<white> • Cheapest to priciest"),
                        (sortType == SortType.PRICE_HIGH ? "<green> • Priciest to cheapest" : "<white> • Priciest to cheapest"),
                        "",
                        "<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>Click</gradient></bold><white> » <color:#B1F5F8>Change sorting</color>"
                )
                .build();
        inventory.setItem(46, sortingButton);

        if (page > 0) {
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
        }

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

        int maxPages = (int) Math.ceil((double) totalItems / 45);
        if ((page + 1) < maxPages) {
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
        }

        ItemStack historyButton = new ItemBuilder(Material.WRITABLE_BOOK)
                .name("<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>ʜɪѕᴛᴏʀʏ</gradient></bold>")
                .lore(
                        "<white>Click here to view your",
                        "<white>auction house history!"
                )
                .build();
        inventory.setItem(53, historyButton);
    }

    private ItemStack buildDisplayItem(AuctionItem auction) {
        ItemStack item = auction.getItem().clone();
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        List<Component> lore = meta.hasLore() && meta.lore() != null ? new ArrayList<>(meta.lore()) : new ArrayList<>();

        lore.add(Component.empty());
        lore.add(MM.deserialize("<gray>Seller: <color:#B1F5F8>" + auction.getSellerName() + "</color>"));
        lore.add(MM.deserialize("<gray>Price: <green>$" + PRICE_FORMAT.format(auction.getPrice()) + "</green>"));
        lore.add(MM.deserialize("<gray>Expires in: <yellow>" + formatRemainingTime(auction.getExpiresAt()) + "</yellow>"));
        lore.add(Component.empty());
        lore.add(MM.deserialize("<bold><gradient:#6FFFFF:#81FFFA:#6FFFFF>Click</gradient></bold><white> » <color:#B1F5F8>Buy Item</color>"));

        meta.lore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public SortType getSortType() { return sortType; }
    public void setSortType(SortType sortType) { this.sortType = sortType; }
    public AuctionItem getAuctionAtSlot(int slot) { return slotMap.get(slot); }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
    private String formatRemainingTime(long expiresAt) {
        long millis = expiresAt - System.currentTimeMillis();
        if (millis <= 0) return "<red>Expired</red>";

        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) return days + "d " + (hours % 24) + "h";
        if (hours > 0) return hours + "h " + (minutes % 60) + "m";
        if (minutes > 0) return minutes + "m " + (seconds % 60) + "s";
        return (seconds % 60) + "s";
    }
}
