package me.outsid.auctionHouse.util;

import org.bukkit.inventory.ItemStack;
import java.util.Base64;

public class ItemSerializer {

    public static String toBase64(ItemStack item) {
        if (item == null || item.getType().isAir()) {
            return null;
        }
        byte[] bytes = item.serializeAsBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public static ItemStack fromBase64(String base64) {
        if (base64 == null || base64.isBlank()) {
            return null;
        }
        byte[] bytes = Base64.getDecoder().decode(base64);
        return ItemStack.deserializeBytes(bytes);
    }
}